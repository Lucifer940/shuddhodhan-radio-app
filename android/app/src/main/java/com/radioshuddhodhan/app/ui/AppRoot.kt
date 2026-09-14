package com.radioshuddhodhan.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.radioshuddhodhan.app.RadioApp
import com.radioshuddhodhan.app.ui.components.AnimatedEqualizer
import com.radioshuddhodhan.app.ui.navigation.Routes
import com.radioshuddhodhan.app.ui.navigation.AppNavHost
import com.radioshuddhodhan.app.ui.theme.RadioShuddhodhanTheme

/**
 * Root composable: theme, locals, splash gate, scaffold (bottom bar +
 * mini-player) and the single NavHost.
 */
@Composable
fun AppRoot(app: RadioApp) {
    // Created directly from the constructor parameter (the CompositionLocal
    // provider below is not active yet at this point).
    val viewModel: AppViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = androidx.lifecycle.viewmodel.viewModelFactory {
            initializer { AppViewModel(app) }
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    RadioShuddhodhanTheme(darkTheme = when (state.themeMode) {
        com.radioshuddhodhan.app.data.ThemeMode.LIGHT -> false
        com.radioshuddhodhan.app.data.ThemeMode.DARK -> true
        com.radioshuddhodhan.app.data.ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
    }) {
        CompositionLocalProvider(
            LocalAppContainer provides app,
            LocalAppStrings provides state.strings
        ) {
            val navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            val playerState by app.playerManager.state.collectAsState()

            val isMainRoute = currentRoute in Routes.bottomBarRoutes
            val showChrome = currentRoute != Routes.SPLASH && currentRoute != Routes.GUIDE

            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                Scaffold(
                    bottomBar = {
                        if (showChrome) {
                            Column {
                                AnimatedVisibility(
                                    visible = playerState.station != null && currentRoute != Routes.PLAYER,
                                    enter = slideInVertically { it } + fadeIn(),
                                    exit = slideOutVertically { it } + fadeOut()
                                ) {
                                    MiniPlayerBar(
                                        title = playerState.station?.name ?: "",
                                        isPlaying = playerState.isPlaying,
                                        isBuffering = playerState.isBuffering || playerState.isConnecting,
                                        onToggle = { app.playerManager.togglePlayPause() },
                                        onOpen = { navController.navigate(Routes.PLAYER) }
                                    )
                                }
                                if (isMainRoute) {
                                    AppBottomBar(
                                        navController = navController,
                                        currentRoute = currentRoute
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavHost(
                            navController = navController,
                            appViewModel = viewModel,
                            appState = state
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppBottomBar(
    navController: NavHostController,
    currentRoute: String?
) {
    val L = LocalAppStrings.current
    NavigationBar {
        bottomItems(L).forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.filledIcon else item.outlinedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}

private data class BottomItem(
    val route: String,
    val label: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
)

private fun bottomItems(L: com.radioshuddhodhan.app.core.language.AppStrings): List<BottomItem> = listOf(
    BottomItem(Routes.HOME, L.home, Icons.Filled.Home, Icons.Outlined.Home),
    BottomItem(Routes.NEWS, L.news, Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    BottomItem(Routes.CALENDAR, L.calendar, Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    BottomItem(Routes.STATIONS, L.stations, Icons.Filled.Radio, Icons.Outlined.Radio),
    BottomItem(Routes.PROFILE, L.profile, Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
private fun MiniPlayerBar(
    title: String,
    isPlaying: Boolean,
    isBuffering: Boolean,
    onToggle: () -> Unit,
    onOpen: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.GraphicEq,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onOpen)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = when {
                        isBuffering -> com.radioshuddhodhan.app.ui.LocalAppStrings.current.buffering
                        else -> com.radioshuddhodhan.app.ui.LocalAppStrings.current.nowPlaying
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }
            AnimatedEqualizer(
                isPlaying = isPlaying,
                barColor = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying)
                        com.radioshuddhodhan.app.ui.LocalAppStrings.current.pause
                    else com.radioshuddhodhan.app.ui.LocalAppStrings.current.play,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
