package com.radioshuddhodhan.app.data.repo

import com.radioshuddhodhan.app.data.SettingsRepository
import com.radioshuddhodhan.app.data.db.AppDatabase
import com.radioshuddhodhan.app.data.db.UserEntity
import com.radioshuddhodhan.app.data.remote.ApiClient
import com.radioshuddhodhan.app.data.remote.AuthRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.util.UUID

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String?) : AuthResult()
}

/**
 * Authentication repository.
 *
 * Two modes:
 *  1. DEMO (no backend URL configured): accounts are stored locally in Room
 *     with a salted SHA-256 password hash. The UI clearly labels this as a
 *     device-only account.
 *  2. BACKEND: register/login call the server, which returns a bearer token.
 *     The token is kept in DataStore and attached to API requests.
 *
 * Google / Facebook sign-in require backend-provided OAuth configuration and
 * are gated behind remote-config flags; no client IDs or secrets are stored
 * in this app.
 */
class AuthRepository(
    private val db: AppDatabase,
    private val settings: SettingsRepository
) {

    /** Currently signed-in user (guest included), or null. */
    val currentUser: Flow<UserEntity?> = settings.currentUserId.map { id ->
        if (id.isBlank()) null else db.userDao().getById(id)
    }

    suspend fun hasBackend(): Boolean = settings.backendUrl.first().isNotBlank()

    // ---------------- Guest ----------------

    suspend fun continueAsGuest(): UserEntity {
        val guest = UserEntity(
            id = UUID.randomUUID().toString(),
            name = "Guest",
            email = null,
            phone = null,
            isGuest = true,
            createdAt = System.currentTimeMillis(),
            passwordHash = null,
            remoteToken = null,
            avatarUrl = null
        )
        db.userDao().upsert(guest)
        settings.setCurrentUserId(guest.id)
        return guest
    }

    // ---------------- Local (demo) accounts ----------------

    suspend fun registerLocal(
        name: String,
        email: String,
        phone: String,
        password: String
    ): AuthResult {
        val existingEmail = email.takeIf { it.isNotBlank() }?.let { db.userDao().getByEmail(it) }
        val existingPhone = phone.takeIf { it.isNotBlank() }?.let { db.userDao().getByPhone(it) }
        if (existingEmail != null || existingPhone != null) {
            return AuthResult.Error("exists")
        }
        val user = UserEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email.takeIf { it.isNotBlank() },
            phone = phone.takeIf { it.isNotBlank() },
            isGuest = false,
            createdAt = System.currentTimeMillis(),
            passwordHash = hashPassword(password),
            remoteToken = null,
            avatarUrl = null
        )
        db.userDao().upsert(user)
        settings.setCurrentUserId(user.id)
        return AuthResult.Success(user)
    }

    suspend fun loginLocal(identifier: String, password: String): AuthResult {
        val user = db.userDao().getByEmail(identifier.trim())
            ?: db.userDao().getByPhone(identifier.trim())
            ?: return AuthResult.Error("notfound")
        if (user.passwordHash != hashPassword(password)) return AuthResult.Error("badpass")
        settings.setCurrentUserId(user.id)
        return AuthResult.Success(user)
    }

    // ---------------- Backend accounts ----------------

    suspend fun registerRemote(
        name: String,
        email: String,
        phone: String,
        password: String
    ): AuthResult {
        val url = settings.backendUrl.first()
        if (url.isBlank()) return AuthResult.Error("no-backend")
        return runCatching {
            val api = ApiClient.create(url)
            val response = api.register(
                AuthRequest(
                    name = name.takeIf { it.isNotBlank() },
                    email = email.takeIf { it.isNotBlank() },
                    phone = phone.takeIf { it.isNotBlank() },
                    password = password
                )
            )
            val user = UserEntity(
                id = response.userId.ifBlank { UUID.randomUUID().toString() },
                name = response.name.ifBlank { name },
                email = email.takeIf { it.isNotBlank() },
                phone = phone.takeIf { it.isNotBlank() },
                isGuest = false,
                createdAt = System.currentTimeMillis(),
                passwordHash = null,
                remoteToken = response.token,
                avatarUrl = null
            )
            db.userDao().upsert(user)
            settings.setAuthToken(response.token)
            settings.setCurrentUserId(user.id)
            AuthResult.Success(user)
        }.getOrElse { AuthResult.Error(it.message) }
    }

    suspend fun loginRemote(identifier: String, password: String): AuthResult {
        val url = settings.backendUrl.first()
        if (url.isBlank()) return AuthResult.Error("no-backend")
        return runCatching {
            val api = ApiClient.create(url)
            val response = api.login(
                AuthRequest(
                    email = identifier.takeIf { it.contains('@') },
                    phone = identifier.takeIf { !it.contains('@') },
                    password = password
                )
            )
            val user = UserEntity(
                id = response.userId.ifBlank { UUID.randomUUID().toString() },
                name = response.name.ifBlank { identifier },
                email = response.email,
                phone = response.phone,
                isGuest = false,
                createdAt = System.currentTimeMillis(),
                passwordHash = null,
                remoteToken = response.token,
                avatarUrl = null
            )
            db.userDao().upsert(user)
            settings.setAuthToken(response.token)
            settings.setCurrentUserId(user.id)
            AuthResult.Success(user)
        }.getOrElse { AuthResult.Error(it.message) }
    }

    // ---------------- Admin login ----------------

    /**
     * Administrator login against the backend (server-side authorisation).
     * In demo mode the on-device PIN set by the user is used instead — the
     * PIN never leaves the device and is never a server credential.
     */
    suspend fun adminLogin(pinOrPassword: String): AuthResult {
        val url = settings.backendUrl.first()
        if (url.isNotBlank()) {
            val result = runCatching {
                val api = ApiClient.create(url)
                api.adminLogin(AuthRequest(password = pinOrPassword))
            }.getOrElse { return AuthResult.Error(it.message) }
            settings.setAdminToken(result.token)
            return AuthResult.Success(
                UserEntity(
                    id = "admin",
                    name = "Administrator",
                    email = null,
                    phone = null,
                    isGuest = false,
                    createdAt = 0,
                    passwordHash = null,
                    remoteToken = result.token,
                    avatarUrl = null
                )
            )
        }
        // Demo mode: on-device PIN.
        return if (settings.checkAdminPin(pinOrPassword)) {
            AuthResult.Success(
                UserEntity(
                    id = "admin",
                    name = "Demo Administrator",
                    email = null,
                    phone = null,
                    isGuest = false,
                    createdAt = 0,
                    passwordHash = null,
                    remoteToken = null,
                    avatarUrl = null
                )
            )
        } else {
            AuthResult.Error("badpin")
        }
    }

    suspend fun logout() {
        settings.setAuthToken("")
        settings.setAdminToken("")
        settings.setCurrentUserId("")
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest("rs-user-salt:$password".toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
