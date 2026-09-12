package com.radioshuddhodhan.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.data.repo.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Login / register state machine.
 */
class AuthViewModel(private val app: RadioApp) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false,
        val hasBackend: Boolean = false,
        val googleEnabled: Boolean = false,
        val facebookEnabled: Boolean = false,
        val phoneEnabled: Boolean = true
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init {
        viewModelScope.launch {
            val hasBackend = app.authRepository.hasBackend()
            val config = app.configRepository.current()
            _state.value = _state.value.copy(
                hasBackend = hasBackend,
                googleEnabled = config.googleLoginEnabled,
                facebookEnabled = config.facebookLoginEnabled,
                phoneEnabled = config.phoneLoginEnabled
            )
        }
    }

    fun login(identifier: String, password: String) {
        if (identifier.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(error = "fields")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = app.authRepository.login(
                identifier, password, _state.value.hasBackend
            )
            _state.value = when (result) {
                is AuthResult.Success -> _state.value.copy(isLoading = false, success = true)
                is AuthResult.Error -> _state.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun register(name: String, email: String, phone: String, password: String, confirm: String) {
        if (name.isBlank() || password.isBlank() || (email.isBlank() && phone.isBlank())) {
            _state.value = _state.value.copy(error = "fields")
            return
        }
        if (password.length < 6) {
            _state.value = _state.value.copy(error = "short")
            return
        }
        if (password != confirm) {
            _state.value = _state.value.copy(error = "mismatch")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = if (_state.value.hasBackend) {
                app.authRepository.registerRemote(name, email, phone, password)
            } else {
                app.authRepository.registerLocal(name, email, phone, password)
            }
            _state.value = when (result) {
                is AuthResult.Success -> _state.value.copy(isLoading = false, success = true)
                is AuthResult.Error -> _state.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun continueAsGuest(onDone: () -> Unit) {
        viewModelScope.launch {
            app.authRepository.continueAsGuest()
            onDone()
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
