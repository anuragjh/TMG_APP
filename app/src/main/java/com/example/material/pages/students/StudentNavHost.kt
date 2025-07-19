/*──────────────────────── 3) NAV‑HOST (UI‑only placeholders) ─────────────────────*/
package com.example.material.pages.students

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.material.datastore.DataStoreManager
import com.example.material.pages.commons.ChatRoomScreen
import com.example.material.pages.commons.NoticesRoute
import com.example.material.pages.commons.PollScreen
import com.example.material.pages.commons.ProfileScreen
import com.example.material.pages.commons.SecurityScreen
import com.example.material.pages.commons.SettingsScreen
import com.example.material.pages.commons.StaticTextScreen
import com.example.material.pages.commons.TMGUpdateScreen
import com.example.material.pages.teacher.Destination
import com.example.material.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import com.example.material.BuildConfig

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StudentNavHost(
    navController    : NavHostController,
    startDestination : StuDest = StuDest.HOME,
    modifier         : Modifier = Modifier
) {
    NavHost(
        navController   = navController,
        startDestination= startDestination.route,
        modifier        = modifier,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left,  tween(300)) + fadeIn(tween(300)) },
        exitTransition  = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) + fadeOut(tween(300)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right,tween(300)) + fadeIn(tween(300)) },
        popExitTransition  = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right,tween(300)) + fadeOut(tween(300)) }
    ) {
        composable(StuDest.SETTINGS.route) {

            SettingsScreen(
                onNavigateToStaticPage = { heading, content ->
                    navController.navigate(StuDest.static_pages.createRoute(heading, content))
                },
                onMyAccClick = {  navController.navigate(StuDest.profile.route) },
                onUpdateClick = { navController.navigate(StuDest.update.route) },
                onSecurityClick = { navController.navigate(StuDest.security.route) },
            )
        }


        composable(
            route = StuDest.chatRoom.route,
            arguments = listOf(
                navArgument("className") { type = NavType.StringType },
                navArgument("canEveryoneMessage") { type = NavType.BoolType },
                navArgument("username") { type = NavType.StringType}
            )
        ) { backStackEntry ->
            val className = backStackEntry.arguments?.getString("className") ?: return@composable
            val canEveryoneMessage = backStackEntry.arguments?.getBoolean("canEveryoneMessage") ?: false
            val username = backStackEntry.arguments?.getString("username")

            ChatRoomScreen(
                className = className,
                canEveryoneMessage = canEveryoneMessage
                , username = username ?: "Unknown",
                onBack = { navController.popBackStack() }
            )
        }


        composable(StuDest.NOTES.route) {

            NotesStudentScreen(
            )
        }

        composable(StuDest.HOME.route) {

            StudentReportScreen(

                onBellClick = {navController.navigate(StuDest.notices.route)},
                onPollClick = {navController.navigate(StuDest.polls.route)},
                onRoutineClick = { navController.navigate(StuDest.routine.route) },
            )
        }

        composable(StuDest.CHATS.route) {
            StudentChatScreen(
                onChatClick = { room ->
                    navController.navigate(
                        StuDest.chatRoom.withArgs(room.className, room.canEveryoneMessage,room.username)
                    )
                }
            )
        }
        composable(StuDest.security.route) {
            SecurityScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(StuDest.routine.route) {
            RoutineScreen(
                onBack = { navController.popBackStack() }
            )
        }


        composable(StuDest.notices.route) {
            NoticesRoute(onBack = { navController.popBackStack() })
        }

        composable(StuDest.polls.route) {
            PollScreen(onBack = { navController.popBackStack() })
        }
        composable(StuDest.update.route) {
            TMGUpdateScreen(
                version = BuildConfig.VERSION_NAME,
                onBack = { navController.popBackStack() }
            )
        }


        composable(StuDest.profile.route) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val appViewModel: AppViewModel = hiltViewModel()
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogoutClick = {
                    coroutineScope.launch {
                        DataStoreManager(context).clearAuth()
                        appViewModel.logout()
                        Toast.makeText(
                            context,
                            "RERUN APP CLOSE-OPEN APP ONCE TO LOGIN",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }

        composable(
            route = StuDest.static_pages.route,
            arguments = listOf(
                navArgument("heading") { type = NavType.StringType },
                navArgument("content") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val heading = backStackEntry.arguments?.getString("heading") ?: "Info"
            val content = backStackEntry.arguments?.getString("content") ?: ""

            StaticTextScreen(
                onBack = { navController.popBackStack() },
                heading = heading,
                content = content
            )
        }

        /* ---------- example leaf page (static text) ---------- */
        composable(
            "static/{heading}/{content}",
            arguments = listOf(
                navArgument("heading"){ type = NavType.StringType },
                navArgument("content"){ type = NavType.StringType }
            )
        ) { backStack ->
            StaticTextScreen(
                heading = backStack.arguments?.getString("heading") ?: "",
                content = backStack.arguments?.getString("content") ?: "",
                onBack  = { navController.popBackStack() }
            )
        }
    }
}




@Composable
private fun CenterText(text: String) = Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
) { Text(text, style = MaterialTheme.typography.headlineMedium) }
