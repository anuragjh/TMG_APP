package com.example.material.pages.teacher


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun TeacherHomeScreen() {
    val navController = rememberNavController() // ✅ Use local navController
    val bottomNavItems = Destination.bottomNavItems
    var selectedDestinationIndex by rememberSaveable { mutableIntStateOf(0) }

    val currentRoute by navController.currentBackStackEntryAsState()
    val showBottomBar = remember(currentRoute) {
        currentRoute?.destination?.route in bottomNavItems.map { it.route }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            selected = selectedDestinationIndex == index,
                            onClick = {
                                selectedDestinationIndex = index
                                navController.navigate(destination.route) {
                                    launchSingleTop = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.contentDescription
                                )
                            },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        TeacherNavHost(
            navController = navController,
            startDestination = bottomNavItems[selectedDestinationIndex],
            modifier = Modifier.padding(innerPadding)
        )
    }
}
