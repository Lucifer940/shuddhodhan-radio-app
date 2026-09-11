package com.radioshuddhodhan.app.ui.navigation

/**
 * Navigation routes. Single-activity architecture: every screen is a
 * composable destination in one NavHost.
 */
object Routes {
    const val SPLASH = "splash"
    const val GUIDE = "guide"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Main (bottom bar)
    const val HOME = "home"
    const val NEWS = "news"
    const val CALENDAR = "calendar"
    const val STATIONS = "stations"
    const val PROFILE = "profile"

    // Secondary
    const val PLAYER = "player"
    const val POSTS = "posts"
    const val HELPDESK = "helpdesk"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
    const val NOTIFICATIONS = "notifications"
    const val SOCIAL = "social"

    // Admin
    const val ADMIN_LOGIN = "adminLogin"
    const val ADMIN_HOME = "adminHome"
    const val ADMIN_NEWS = "adminNews"
    const val ADMIN_POSTS = "adminPosts"
    const val ADMIN_STATIONS = "adminStations"
    const val ADMIN_EVENTS = "adminEvents"
    const val ADMIN_ANNOUNCEMENTS = "adminAnnouncements"
    const val ADMIN_SOCIAL = "adminSocial"
    const val ADMIN_HELPDESK = "adminHelpdesk"
    const val ADMIN_NOTIFY = "adminNotify"
    const val ADMIN_CONFIG = "adminConfig"
    const val ADMIN_AI = "adminAi"

    fun newsDetail(id: String) = "newsDetail/$id"
    fun postDetail(id: String) = "postDetail/$id"

    /** Bottom-bar destinations in display order. */
    val bottomBarRoutes = listOf(HOME, NEWS, CALENDAR, STATIONS, PROFILE)
}
