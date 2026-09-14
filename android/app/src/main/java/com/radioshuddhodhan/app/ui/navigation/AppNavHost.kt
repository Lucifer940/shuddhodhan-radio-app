package com.radioshuddhodhan.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.radioshuddhodhan.app.ui.AppStateHolder
import com.radioshuddhodhan.app.ui.AppViewModel
import com.radioshuddhodhan.app.ui.about.AboutScreen
import com.radioshuddhodhan.app.ui.admin.AdminAiScreen
import com.radioshuddhodhan.app.ui.admin.AdminAnnouncementsScreen
import com.radioshuddhodhan.app.ui.admin.AdminConfigScreen
import com.radioshuddhodhan.app.ui.admin.AdminEventsScreen
import com.radioshuddhodhan.app.ui.admin.AdminHelpdeskScreen
import com.radioshuddhodhan.app.ui.admin.AdminManagerScreen
import com.radioshuddhodhan.app.ui.admin.AdminNewsScreen
import com.radioshuddhodhan.app.ui.admin.AdminNotifyScreen
import com.radioshuddhodhan.app.ui.admin.AdminSocialScreen
import com.radioshuddhodhan.app.ui.admin.AdminStationsScreen
import com.radioshuddhodhan.app.ui.auth.LoginScreen
import com.radioshuddhodhan.app.ui.auth.RegisterScreen
import com.radioshuddhodhan.app.ui.calendar.CalendarScreen
import com.radioshuddhodhan.app.ui.helpdesk.HelpdeskScreen
import com.radioshuddhodhan.app.ui.home.HomeScreen
import com.radioshuddhodhan.app.ui.news.NewsDetailScreen
import com.radioshuddhodhan.app.ui.news.NewsScreen
import com.radioshuddhodhan.app.ui.notifications.NotificationsScreen
import com.radioshuddhodhan.app.ui.onboarding.GuideScreen
import com.radioshuddhodhan.app.ui.player.PlayerScreen
import com.radioshuddhodhan.app.ui.posts.PostDetailScreen
import com.radioshuddhodhan.app.ui.posts.PostsScreen
import com.radioshuddhodhan.app.ui.profile.ProfileScreen
import com.radioshuddhodhan.app.ui.settings.SettingsScreen
import com.radioshuddhodhan.app.ui.social.SocialScreen
import com.radioshuddhodhan.app.ui.splash.SplashScreen
import com.radioshuddhodhan.app.ui.stations.StationsScreen

/**
 * The single NavHost holding every destination of the app.
 * Screens slide + fade horizontally for a smooth native feel.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    appViewModel: AppViewModel,
    appState: AppStateHolder
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = { slideInHorizontally(tween(280)) { it / 6 } + fadeIn(tween(280)) },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(220)) },
        popExitTransition = { slideOutHorizontally(tween(240)) { it / 6 } + fadeOut(tween(240)) }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                isFirstRun = appState.isFirstRun,
                onFinished = { target ->
                    if (navController.currentDestination?.route == Routes.SPLASH) {
                        navController.navigate(target) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.GUIDE) {
            GuideScreen(
                onDone = {
                    appViewModel.markFirstRunDone()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.GUIDE) { inclusive = true }
                    }
                },
                onSkipToLogin = {
                    appViewModel.markFirstRunDone()
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.GUIDE) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegister = { navController.navigate(Routes.REGISTER) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegistered = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.GUIDE) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onOpenPlayer = { navController.navigate(Routes.PLAYER) },
                onOpenNewsDetail = { navController.navigate(Routes.newsDetail(it)) },
                onOpenPostDetail = { navController.navigate(Routes.postDetail(it)) },
                onOpenAllNews = { navController.navigate(Routes.NEWS) },
                onOpenAllPosts = { navController.navigate(Routes.POSTS) },
                onOpenStations = { navController.navigate(Routes.STATIONS) },
                onOpenCalendar = { navController.navigate(Routes.CALENDAR) },
                onOpenHelpdesk = { navController.navigate(Routes.HELPDESK) },
                onOpenSocial = { navController.navigate(Routes.SOCIAL) }
            )
        }

        composable(Routes.NEWS) {
            NewsScreen(onOpenDetail = { navController.navigate(Routes.newsDetail(it)) })
        }

        composable(
            route = "newsDetail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id") ?: ""
            NewsDetailScreen(
                newsId = id,
                onBack = { navController.popBackStack() },
                onOpenOther = { navController.navigate(Routes.newsDetail(it)) }
            )
        }

        composable(Routes.CALENDAR) {
            CalendarScreen()
        }

        composable(Routes.STATIONS) {
            StationsScreen(onOpenPlayer = { navController.navigate(Routes.PLAYER) })
        }

        composable(Routes.PLAYER) {
            PlayerScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.POSTS) {
            PostsScreen(onOpenDetail = { navController.navigate(Routes.postDetail(it)) })
        }

        composable(
            route = "postDetail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id") ?: ""
            PostDetailScreen(
                postId = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onLogin = { navController.navigate(Routes.LOGIN) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenAbout = { navController.navigate(Routes.ABOUT) },
                onOpenBookmarks = { navController.navigate(Routes.NEWS) },
                onOpenFavorites = { navController.navigate(Routes.STATIONS) },
                onOpenNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                onOpenHelpdesk = { navController.navigate(Routes.HELPDESK) },
                onOpenSocial = { navController.navigate(Routes.SOCIAL) }
            )
        }

        composable(Routes.HELPDESK) {
            HelpdeskScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenAbout = { navController.navigate(Routes.ABOUT) },
                onOpenAdmin = { navController.navigate(Routes.ADMIN_HOME) }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(
                onBack = { navController.popBackStack() },
                onOpenSocial = { navController.navigate(Routes.SOCIAL) }
            )
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SOCIAL) {
            SocialScreen(onBack = { navController.popBackStack() })
        }

        // ---- Admin (reachable ONLY from Settings when the signed-in
        // ---- account is the station owner; invisible to everyone else) ----
        composable(Routes.ADMIN_HOME) {
            AdminManagerScreen(navController = navController)
        }

        composable(Routes.ADMIN_NEWS) { AdminNewsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.ADMIN_POSTS) { AdminNewsScreen(postMode = true, onBack = { navController.popBackStack() }) }
        composable(Routes.ADMIN_STATIONS) { AdminStationsScreen() }
        composable(Routes.ADMIN_EVENTS) { AdminEventsScreen() }
        composable(Routes.ADMIN_ANNOUNCEMENTS) { AdminAnnouncementsScreen() }
        composable(Routes.ADMIN_SOCIAL) { AdminSocialScreen() }
        composable(Routes.ADMIN_HELPDESK) { AdminHelpdeskScreen() }
        composable(Routes.ADMIN_NOTIFY) { AdminNotifyScreen() }
        composable(Routes.ADMIN_CONFIG) { AdminConfigScreen() }
        composable(Routes.ADMIN_AI) { AdminAiScreen() }
    }
}
