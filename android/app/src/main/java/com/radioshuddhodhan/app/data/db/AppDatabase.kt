package com.radioshuddhodhan.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        NewsEntity::class,
        PostEntity::class,
        StationEntity::class,
        EventEntity::class,
        AnnouncementEntity::class,
        HelpdeskTicketEntity::class,
        SocialLinkEntity::class,
        AppConfigEntity::class,
        UserEntity::class,
        FavoriteStationEntity::class,
        BookmarkedNewsEntity::class,
        NotificationItemEntity::class
    ],
    version = 2, // v2: users.isAdmin
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun postDao(): PostDao
    abstract fun stationDao(): StationDao
    abstract fun eventDao(): EventDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun helpdeskDao(): HelpdeskDao
    abstract fun socialLinkDao(): SocialLinkDao
    abstract fun configDao(): ConfigDao
    abstract fun userDao(): UserDao
    abstract fun favoriteStationDao(): FavoriteStationDao
    abstract fun bookmarkedNewsDao(): BookmarkedNewsDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "radio_shuddhodhan.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}
