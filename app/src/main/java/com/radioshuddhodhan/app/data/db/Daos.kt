package com.radioshuddhodhan.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news WHERE isPublished = 1 ORDER BY isBreaking DESC, publishedAt DESC")
    fun observePublished(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news ORDER BY isBreaking DESC, updatedAt DESC")
    fun observeAll(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE id = :id")
    fun observeById(id: String): Flow<NewsEntity?>

    @Query("SELECT * FROM news WHERE id = :id")
    suspend fun getById(id: String): NewsEntity?

    @Query(
        "SELECT * FROM news WHERE isPublished = 1 AND (title LIKE '%' || :q || '%' OR summary LIKE '%' || :q || '%' OR category LIKE '%' || :q || '%') ORDER BY publishedAt DESC"
    )
    fun searchPublished(q: String): Flow<List<NewsEntity>>

    @Query("SELECT DISTINCT category FROM news WHERE isPublished = 1 ORDER BY category")
    fun observeCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<NewsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: NewsEntity)

    @Update
    suspend fun update(item: NewsEntity)

    @Query("DELETE FROM news WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM news WHERE source = 'remote' AND id NOT IN (:keepIds)")
    suspend fun deleteRemoteNotIn(keepIds: List<String>)

    @Query("DELETE FROM news")
    suspend fun deleteAll()
}

@Dao
interface PostDao {
    @Query("SELECT * FROM posts WHERE isPublished = 1 ORDER BY publishedAt DESC")
    fun observePublished(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id")
    fun observeById(id: String): Flow<PostEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: PostEntity)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM posts WHERE source = 'remote' AND id NOT IN (:keepIds)")
    suspend fun deleteRemoteNotIn(keepIds: List<String>)
}

@Dao
interface StationDao {
    @Query("SELECT * FROM stations ORDER BY isFeatured DESC, sortOrder ASC, name ASC")
    fun observeAll(): Flow<List<StationEntity>>

    @Query("SELECT * FROM stations WHERE isEnabled = 1 ORDER BY isFeatured DESC, sortOrder ASC, name ASC")
    fun observeEnabled(): Flow<List<StationEntity>>

    @Query("SELECT * FROM stations WHERE id = :id")
    suspend fun getById(id: String): StationEntity?

    @Query(
        "SELECT * FROM stations WHERE isEnabled = 1 AND (name LIKE '%' || :q || '%' OR nameNe LIKE '%' || :q || '%' OR description LIKE '%' || :q || '%') ORDER BY sortOrder ASC"
    )
    fun searchEnabled(q: String): Flow<List<StationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<StationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: StationEntity)

    @Query("DELETE FROM stations WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM stations WHERE source = 'remote' AND id NOT IN (:keepIds)")
    suspend fun deleteRemoteNotIn(keepIds: List<String>)
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY adDate ASC")
    fun observeAll(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE adDate >= :todayIso ORDER BY adDate ASC LIMIT :limit")
    fun observeUpcoming(todayIso: String, limit: Int): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE adDate = :dateIso ORDER BY title ASC")
    fun observeForDate(dateIso: String): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: EventEntity)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM events WHERE source = 'remote' AND id NOT IN (:keepIds)")
    suspend fun deleteRemoteNotIn(keepIds: List<String>)
}

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements WHERE isActive = 1 AND activeUntil >= :now ORDER BY createdAt DESC")
    fun observeActive(now: Long): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: AnnouncementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<AnnouncementEntity>)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM announcements WHERE source = 'remote' AND id NOT IN (:keepIds)")
    suspend fun deleteRemoteNotIn(keepIds: List<String>)
}

@Dao
interface HelpdeskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: HelpdeskTicketEntity)

    @Query("SELECT * FROM helpdesk_tickets ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<HelpdeskTicketEntity>>

    @Query("SELECT * FROM helpdesk_tickets WHERE contact = :contact ORDER BY createdAt DESC")
    fun observeForContact(contact: String): Flow<List<HelpdeskTicketEntity>>

    @Query("DELETE FROM helpdesk_tickets WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface SocialLinkDao {
    @Query("SELECT * FROM social_links WHERE isEnabled = 1 ORDER BY sortOrder ASC")
    fun observeEnabled(): Flow<List<SocialLinkEntity>>

    @Query("SELECT * FROM social_links ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<SocialLinkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<SocialLinkEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: SocialLinkEntity)

    @Query("DELETE FROM social_links WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM social_links WHERE source = 'remote' AND id NOT IN (:keepIds)")
    suspend fun deleteRemoteNotIn(keepIds: List<String>)
}

@Dao
interface ConfigDao {
    @Query("SELECT * FROM app_config WHERE id = 1")
    fun observe(): Flow<AppConfigEntity?>

    @Query("SELECT * FROM app_config WHERE id = 1")
    suspend fun get(): AppConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AppConfigEntity)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE isGuest = 0")
    suspend fun getNonGuests(): List<UserEntity>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email AND isGuest = 0 LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone AND isGuest = 0 LIMIT 1")
    suspend fun getByPhone(phone: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface FavoriteStationDao {
    @Query("SELECT * FROM favorite_stations")
    fun observeAll(): Flow<List<FavoriteStationEntity>>

    @Query("SELECT stationId FROM favorite_stations")
    fun observeIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: FavoriteStationEntity)

    @Query("DELETE FROM favorite_stations WHERE stationId = :stationId")
    suspend fun remove(stationId: String)
}

@Dao
interface BookmarkedNewsDao {
    @Query("SELECT * FROM bookmarked_news ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<BookmarkedNewsEntity>>

    @Query("SELECT newsId FROM bookmarked_news")
    fun observeIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: BookmarkedNewsEntity)

    @Query("DELETE FROM bookmarked_news WHERE newsId = :newsId")
    suspend fun remove(newsId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY receivedAt DESC")
    fun observeAll(): Flow<List<NotificationItemEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun observeUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: NotificationItemEntity)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllRead()

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}
