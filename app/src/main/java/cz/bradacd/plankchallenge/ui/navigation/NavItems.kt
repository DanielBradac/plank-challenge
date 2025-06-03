package cz.bradacd.plankchallenge.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem (
    val label: String,
    val icon: ImageVector,
    val route: String,
)

val navItems = listOf(
    NavItem(
        label = "Stopwatch",
        icon = Icons.Default.WatchLater,
        route = Screens.StopWatchScreen.name
    ),
    NavItem(
        label = "Your planks",
        icon = Icons.Default.Star,
        route = Screens.LogScreen.name
    ),
    NavItem(
        label = "Settings",
        icon = Icons.Default.Settings,
        route = Screens.SettingsScreen.name
    )
)