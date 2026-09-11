package com.radioshuddhodhan.app.data.repo

import com.radioshuddhodhan.app.data.SettingsRepository
import com.radioshuddhodhan.app.data.db.AppDatabase
import com.radioshuddhodhan.app.data.db.HelpdeskTicketEntity
import com.radioshuddhodhan.app.data.remote.ApiClient
import com.radioshuddhodhan.app.data.remote.HelpdeskRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

/**
 * Helpdesk repository. Tickets go to the backend when configured; otherwise
 * they queue in the local database (demo mode) where the on-device admin
 * console can read and answer them.
 */
class HelpdeskRepository(
    private val db: AppDatabase,
    private val settings: SettingsRepository
) {

    fun observeAllTickets(): Flow<List<HelpdeskTicketEntity>> = db.helpdeskDao().observeAll()

    fun observeTicketsForContact(contact: String): Flow<List<HelpdeskTicketEntity>> =
        db.helpdeskDao().observeForContact(contact)

    suspend fun submit(
        name: String,
        contact: String,
        category: String,
        message: String
    ): Boolean {
        // Always keep a local copy (queue) so the user sees their request.
        db.helpdeskDao().upsert(
            HelpdeskTicketEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                contact = contact,
                category = category,
                message = message,
                createdAt = System.currentTimeMillis(),
                status = if (hasBackendNow()) "pending" else "open",
                reply = null,
                repliedAt = null,
                source = "local"
            )
        )
        val url = settings.backendUrl.first()
        if (url.isBlank()) return true // demo mode: queued locally
        return runCatching {
            val api = ApiClient.create(url)
            api.submitHelpdeskTicket(HelpdeskRequest(name, contact, category, message))
            true
        }.getOrDefault(false)
    }

    suspend fun replyTicket(ticket: HelpdeskTicketEntity, reply: String) {
        db.helpdeskDao().upsert(ticket.copy(reply = reply, repliedAt = System.currentTimeMillis(), status = "resolved"))
        val url = settings.backendUrl.first()
        if (url.isBlank()) return
        runCatching {
            val api = ApiClient.create(url) { settings.adminToken.first() }
            api.updateHelpdeskTicket(
                ticket.id,
                com.radioshuddhodhan.app.data.remote.HelpdeskTicketDto(
                    id = ticket.id, name = ticket.name, contact = ticket.contact,
                    category = ticket.category, message = ticket.message,
                    createdAt = ticket.createdAt, status = "resolved",
                    reply = reply, repliedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun deleteTicket(id: String) = db.helpdeskDao().delete(id)

    private suspend fun hasBackendNow(): Boolean = settings.backendUrl.first().isNotBlank()
}
