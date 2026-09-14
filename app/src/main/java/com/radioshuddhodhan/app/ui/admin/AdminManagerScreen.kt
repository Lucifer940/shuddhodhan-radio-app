package com.radioshuddhodhan.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.radioshuddhodhan.app.ui.LocalAppStrings
import com.radioshuddhodhan.app.ui.navigation.Routes
import com.radioshuddhodhan.app.ui.components.brandGradient

/**
 * Admin dashboard: entry tiles for every management area. All changes are
 * written to the local database instantly (so user screens update in real
 * time on this device) and pushed to the backend when one is configured.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManagerScreen(navController: NavHostController) {
    val L = LocalAppStrings.current

    data class Tile(val route: String, val label: String, val icon: ImageVector)

    val tiles = listOf(
        Tile(Routes.ADMIN_CONFIG, L.remoteConfig, Icons.Filled.Tune),
        Tile(Routes.ADMIN_NEWS, L.manageNews, Icons.Filled.MenuBook),
        Tile(Routes.ADMIN_STATIONS, L.manageStations, Icons.Filled.Radio),
        Tile(Routes.ADMIN_EVENTS, L.manageEvents, Icons.Filled.Event),
        Tile(Routes.ADMIN_ANNOUNCEMENTS, L.manageAnnouncements, Icons.Filled.Campaign),
        Tile(Routes.ADMIN_SOCIAL, L.manageSocial, Icons.Filled.Share),
        Tile(Routes.ADMIN_HELPDESK, L.helpdeskInbox, Icons.Filled.SupportAgent),
        Tile(Routes.ADMIN_NOTIFY, L.sendNotification, Icons.Filled.NotificationsActive),
        Tile(Routes.ADMIN_AI, L.aiAssistant, Icons.Filled.AutoAwesome)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(L.adminDashboard, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Icon(
                        Icons.Filled.AdminPanelSettings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(Icons.Filled.AdminPanelSettings, contentDescription = null)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = L.demoAdminNotice,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 110.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(tiles) { tile ->
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.height(120.dp),
                        onClick = { navController.navigate(tile.route) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        ) {
                            Icon(
                                tile.icon,
                                contentDescription = tile.label,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = tile.label,
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}
