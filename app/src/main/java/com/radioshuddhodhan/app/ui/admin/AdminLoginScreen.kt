package com.radioshuddhodhan.app.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.appViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private class AdminLoginViewModel(private val app: RadioApp) : ViewModel() {

    data class LoginState(
        val loading: Boolean = false,
        val error: Boolean = false,
        val success: Boolean = false,
        val needsPinSetup: Boolean = false,
        val hasBackend: Boolean = false
    )

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    init {
        viewModelScope.launch {
            val hasPin = app.settings.hasAdminPin()
            val hasBackend = app.authRepository.hasBackend()
            _state.value = LoginState(needsPinSetup = !hasPin, hasBackend = hasBackend)
        }
    }

    /** Demo mode: set the on-device PIN for the first time. */
    fun setPin(pin: String, confirm: String) {
        if (pin.length < 4 || pin != confirm) {
            _state.value = _state.value.copy(error = true)
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = false)
            app.settings.setAdminPin(pin)
            _state.value = _state.value.copy(loading = false, success = true)
        }
    }

    fun login(pinOrPassword: String) {
        if (pinOrPassword.isBlank()) {
            _state.value = _state.value.copy(error = true)
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = false)
            val result = app.authRepository.adminLogin(pinOrPassword)
            _state.value = when (result) {
                is com.radioshuddhodhan.app.data.repo.AuthResult.Success ->
                    _state.value.copy(loading = false, success = true)
                is com.radioshuddhodhan.app.data.repo.AuthResult.Error ->
                    _state.value.copy(loading = false, error = true)
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = false)
    }
}

/**
 * Administrator login.
 *
 * Backend mode: password is verified by the SERVER (docs/BACKEND_API.md) —
 * no credentials are ever stored in the app.
 * Demo mode: a PIN is set on first use and hashed on this device only.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val L = LocalAppStrings.current
    val viewModel: AdminLoginViewModel = appViewModel { AdminLoginViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    var pin by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(state.success) {
        if (state.success) onSuccess()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(L.adminLoginTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = L.back)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.AdminPanelSettings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = L.adminLoginTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = L.demoAdminNotice,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(Modifier.height(24.dp))

            if (state.needsPinSetup && !state.hasBackend) {
                // First-use PIN setup (demo mode)
                Text(L.setNewPin, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it; viewModel.clearError() },
                    label = { Text(L.adminPin) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirm,
                    onValueChange = { confirm = it; viewModel.clearError() },
                    label = { Text(L.confirmPassword) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.error) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = L.pinMismatch,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.setPin(pin, confirm) },
                    enabled = !state.loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else Text(L.ok)
                }
            } else {
                // PIN (demo) or server password (backend)
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it; viewModel.clearError() },
                    label = {
                        Text(if (state.hasBackend) L.password else L.adminPin)
                    },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.error) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = L.pinMismatch,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.login(pin) },
                    enabled = !state.loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else Text(L.login)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
