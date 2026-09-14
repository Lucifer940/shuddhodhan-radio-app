package com.radioshuddhodhan.app.data.repo

import com.radioshuddhodhan.app.core.AdminSecrets
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

    // ---------------- Unified login (users + owner) ----------------

    /**
     * THE single login path used by the login screen — the same page serves
     * everyone. The station owner's credentials are checked FIRST (digest
     * comparison only; nothing readable is stored). On a match the signed-in
     * account is flagged as admin, which unlocks the admin dashboard inside
     * Settings. For everybody else this behaves as an ordinary login, with no
     * visible difference or hint that an admin account exists.
     */
    suspend fun login(identifier: String, password: String, useBackend: Boolean): AuthResult {
        if (AdminSecrets.isAdminCredentials(identifier, password)) {
            return ensureOwnerAccount(identifier, password)
        }
        return if (useBackend) loginRemote(identifier, password)
        else loginLocal(identifier, password)
    }

    /**
     * Creates/refreshes the local owner account. When a backend is configured
     * the credentials are ALSO exchanged for a server admin token, so real
     * admin APIs stay authorized server-side.
     */
    private suspend fun ensureOwnerAccount(identifier: String, password: String): AuthResult {
        val email = identifier.trim().lowercase()
        val existing = db.userDao().getByEmail(email)
        val user = (existing ?: UserEntity(
            id = "station-owner",
            name = "Station Manager",
            email = email,
            phone = null,
            isGuest = false,
            isAdmin = true,
            createdAt = System.currentTimeMillis(),
            passwordHash = null,
            remoteToken = null,
            avatarUrl = null
        )).copy(isAdmin = true)
        db.userDao().upsert(user)
        settings.setCurrentUserId(user.id)

        // Server-side authorization when a backend is configured.
        val url = settings.backendUrl.first()
        if (url.isNotBlank()) {
            runCatching {
                val api = ApiClient.create(url)
                val response = api.adminLogin(
                    AuthRequest(email = email, password = password)
                )
                if (response.token.isNotBlank()) settings.setAdminToken(response.token)
            }
        }
        return AuthResult.Success(user)
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
