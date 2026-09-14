package com.radioshuddhodhan.app.core

import java.security.MessageDigest

/**
 * Built-in station-owner account.
 *
 * There is NO separate admin login screen and NO PIN: the owner signs in on
 * the SAME login page as everyone else. When — and only when — the entered
 * credentials match the station owner's account does the app unlock the admin
 * dashboard inside Settings. Normal users and guests never see any admin UI,
 * and a wrong password for that e-mail behaves exactly like any other failed
 * login (no hint that the account is special).
 *
 * Security notes:
 *  - Only salted SHA-256 DIGESTS are compiled in — the actual e-mail and
 *    password never appear in the source code or the APK.
 *  - When a backend server is configured, the same credentials are also
 *    exchanged with the server (`api/v1/admin/login`) so REAL authorization
 *    for server-side admin APIs stays on the server (see docs/BACKEND_API.md).
 *  - To rotate the credentials, change them on the backend and update the two
 *    digests below.
 */
object AdminSecrets {

    /** SHA-256("radio-shuddhodhan-admin-id:" + e-mail, lowercased). */
    private const val ID_HASH =
        "50d6d4e26f9a7f53babfa1f6333f2bcb3c7759eff114a14d105e1c2fbae47ac9"

    /** SHA-256("radio-shuddhodhan-admin-key:" + password). */
    private const val KEY_HASH =
        "e8b6ce2f103d4dcee67331b22d82710515814dfaca8d15d7a4199d3291e01629"

    private const val ID_SALT = "radio-shuddhodhan-admin-id:"
    private const val KEY_SALT = "radio-shuddhodhan-admin-key:"

    /** True only when the identifier AND password both match the owner account. */
    fun isAdminCredentials(identifier: String, password: String): Boolean {
        if (identifier.isBlank() || password.isBlank()) return false
        val idOk = MessageDigest.isEqual(
            sha256Hex(ID_SALT + identifier.trim().lowercase()).toByteArray(),
            ID_HASH.toByteArray()
        )
        val keyOk = MessageDigest.isEqual(
            sha256Hex(KEY_SALT + password).toByteArray(),
            KEY_HASH.toByteArray()
        )
        return idOk && keyOk
    }

    private fun sha256Hex(text: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(text.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
}
