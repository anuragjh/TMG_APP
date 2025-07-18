package com.example.material.pages.students



import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.material.pages.students.StuDest.Companion.bottomNavItems

@Composable
fun StudentHomeScreen() {

    /* isolated nav‑controller for the student tab‑bar world */
    val navController            = rememberNavController()
    var selectedIndex   by rememberSaveable { mutableIntStateOf(0) }

    val currentRoute  = navController.currentBackStackEntryAsState().value
        ?.destination?.route
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEachIndexed { idx, dest ->
                        NavigationBarItem(
                            selected = idx == selectedIndex,
                            onClick  = {
                                selectedIndex = idx
                                navController.navigate(dest.route) {
                                    launchSingleTop = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            },
                            icon  = { Icon(dest.icon, dest.contentDescription) },
                            label = { Text(dest.label) }
                        )
                    }
                }
            }
        }
    ) { inner ->
        StudentNavHost(
            navController       = navController,
            startDestination    = bottomNavItems[selectedIndex],
            modifier            = Modifier.padding(inner)
        )
    }
}
