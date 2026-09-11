@file:OptIn(UnstableApi::class)

package com.radioshuddhodhan.app.audio

import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.radioshuddhodhan.app.data.db.StationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * UI-facing player state.
 */
data class PlayerState(
    val station: StationEntity? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val isConnecting: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val retrying: Boolean = false,
    val retryAttempt: Int = 0
)

/**
 * App-scoped connection between the UI and [RadioPlaybackService].
 * Wraps a Media3 MediaController and exposes state as a StateFlow that
 * Compose collects directly.
 *
 * @param stationResolver resolves a station id to its entity so playback can
 *   be restored when the UI process reconnects to an already-running session
 *   (e.g. app reopened while streaming in the background).
 */
class PlayerManager(
    private val context: Context,
    private val scope: CoroutineScope,
    private val stationResolver: suspend (String) -> StationEntity?
) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var controller: MediaController? = null
    private var controllerJob: Job? = null

    /** Controlled by the auto-reconnect audio setting. */
    @Volatile
    var autoReconnectEnabled: Boolean = true

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state

    private var retryJob: Job? = null

    init {
        controllerJob = scope.launch {
            runCatching {
                val token = SessionToken(
                    context,
                    ComponentName(context, RadioPlaybackService::class.java)
                )
                val future = MediaController.Builder(context, token).buildAsync()
                future.addListener(
                    {
                        val c = future.get()
                        controller = c
                        c.addListener(playerListener)
                        // Restore UI state when reconnecting to a live session.
                        val item = c.currentMediaItem
                        if (item != null) {
                            scope.launch {
                                val station = stationResolver(item.mediaId)
                                _state.value = _state.value.copy(
                                    station = station,
                                    isPlaying = c.isPlaying,
                                    isBuffering = c.playbackState == Player.STATE_BUFFERING,
                                    isConnecting = !c.isPlaying &&
                                        c.playbackState == Player.STATE_BUFFERING
                                )
                            }
                        }
                    },
                    MoreExecutors.directExecutor()
                )
            }
        }
    }

    private val playerListener = object : Player.Listener {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.value = _state.value.copy(isPlaying = isPlaying)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val st = _state.value
            _state.value = st.copy(
                isBuffering = playbackState == Player.STATE_BUFFERING,
                isConnecting = playbackState == Player.STATE_IDLE && st.station != null && !st.hasError
            )
        }

        override fun onPlayerError(error: PlaybackException) {
            val st = _state.value
            if (st.station == null) return
            if (autoReconnectEnabled) {
                _state.value = st.copy(
                    hasError = true,
                    errorMessage = error.errorCodeName,
                    retrying = true,
                    retryAttempt = st.retryAttempt + 1
                )
                scheduleReconnect()
            } else {
                _state.value = st.copy(
                    hasError = true,
                    errorMessage = error.errorCodeName,
                    retrying = false
                )
            }
        }
    }

    private fun scheduleReconnect() {
        retryJob?.cancel()
        retryJob = scope.launch {
            val attempt = _state.value.retryAttempt
            if (attempt <= MAX_RETRIES) {
                // Exponential-ish backoff: 2s, 4s, 8s, 16s, 30s
                val backoffMs = when (attempt) {
                    1 -> 2_000L
                    2 -> 4_000L
                    3 -> 8_000L
                    4 -> 16_000L
                    else -> 30_000L
                }
                delay(backoffMs)
                val station = _state.value.station ?: return@launch
                _state.value = _state.value.copy(isConnecting = true)
                startStream(station, resetRetry = false)
            } else {
                _state.value = _state.value.copy(retrying = false, isConnecting = false)
            }
        }
    }

    /** Starts playing the given station's live stream. */
    fun play(station: StationEntity) {
        startStream(station, resetRetry = true)
    }

    private fun startStream(station: StationEntity, resetRetry: Boolean) {
        val c = controller
        if (c == null) {
            // Controller still connecting; retry shortly.
            scope.launch {
                delay(300)
                startStream(station, resetRetry)
            }
            return
        }
        if (station.streamUrl.isBlank()) {
            _state.value = PlayerState(
                station = station,
                hasError = true,
                errorMessage = "STREAM_NOT_CONFIGURED"
            )
            return
        }
        retryJob?.cancel()
        _state.value = _state.value.copy(
            station = station,
            hasError = false,
            errorMessage = null,
            isConnecting = true,
            retrying = false,
            retryAttempt = if (resetRetry) 0 else _state.value.retryAttempt
        )
        val item = MediaItem.Builder()
            .setUri(station.streamUrl)
            .setMediaId(station.id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(station.name)
                    .setArtist("Radio Shuddhodhan")
                    .setLiveConfiguration(
                        MediaMetadata.LiveConfiguration.Builder().build()
                    )
                    .build()
            )
            .build()
        c.setMediaItem(item)
        c.prepare()
        c.play()
    }

    fun togglePlayPause() {
        val c = controller ?: return
        when {
            c.isPlaying -> c.pause()
            c.playbackState == Player.STATE_IDLE &&
                _state.value.station?.streamUrl?.isNotBlank() == true ->
                startStream(_state.value.station!!, resetRetry = true)
            else -> c.play()
        }
    }

    fun stop() {
        retryJob?.cancel()
        val c = controller ?: return
        c.stop()
        c.clearMediaItems()
        _state.value = PlayerState()
    }

    /** Manual retry from the error state. */
    fun retry() {
        val station = _state.value.station ?: return
        startStream(station, resetRetry = true)
    }

    // ---------------- Volume (device media volume) ----------------

    fun currentVolume(): Int = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

    fun maxVolume(): Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    fun setVolume(volume: Int) {
        val clamped = volume.coerceIn(0, maxVolume())
        runCatching {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, clamped, 0)
        }
    }

    fun release() {
        retryJob?.cancel()
        controllerJob?.cancel()
        controller?.let { c ->
            c.removeListener(playerListener)
            c.release()
        }
        controller = null
    }

    companion object {
        private const val MAX_RETRIES = 5
    }
}
