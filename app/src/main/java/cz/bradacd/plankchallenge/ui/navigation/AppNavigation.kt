package cz.bradacd.plankchallenge.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cz.bradacd.plankchallenge.ui.screens.LogScreen
import cz.bradacd.plankchallenge.ui.screens.StopWatchScreen

// Function to determine the slide direction
fun determineSlideDirection(from: NavBackStackEntry?, to: NavBackStackEntry): AnimatedContentTransitionScope.SlideDirection {
    val screenOrder = enumValues<Screens>().map { it.name }
    val fromIndex = screenOrder.indexOf(from?.destination?.route)
    val toIndex = screenOrder.indexOf(to.destination.route)
    return if (fromIndex < toIndex) {
        AnimatedContentTransitionScope.SlideDirection.Left
    } else {
        AnimatedContentTransitionScope.SlideDirection.Right
    }
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { navItem ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true,
                        onClick = {
                            navController.navigate(navItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = null)
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.StopWatchScreen.name,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { slideIntoContainer(determineSlideDirection(initialState, targetState), tween(500)) },
            exitTransition = { slideOutOfContainer(determineSlideDirection(initialState, targetState), tween(500)) },
            popEnterTransition = { slideIntoContainer(determineSlideDirection(initialState, targetState), tween(500)) },
            popExitTransition = { slideOutOfContainer(determineSlideDirection(initialState, targetState), tween(500)) }
        ) {
            composable(route = Screens.StopWatchScreen.name) {
                StopWatchScreen()
            }
            composable(route = Screens.LogScreen.name) {
                LogScreen()
            }
        }
    }
}