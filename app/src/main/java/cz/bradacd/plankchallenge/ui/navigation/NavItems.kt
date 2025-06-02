package cz.bradacd.plankchallenge.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem (
    val label: String,
    val icon: ImageVector,
    val route: String,
)

val navItems = listOf(
    NavItem(
        label = "StopWatchScreen",
        icon = Icons.Default.Home,
        route = Screens.StopWatchScreen.name
    ),
    NavItem(
        label = "LogScreen",
        icon = Icons.Default,
        route = Screens.LogScreen.name
    )
)