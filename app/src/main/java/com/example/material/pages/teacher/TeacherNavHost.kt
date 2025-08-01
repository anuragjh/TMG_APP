package com.example.material.pages.teacher


import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.material.BuildConfig
import com.example.material.datastore.DataStoreManager
import com.example.material.pages.auth.LoginScreen
import com.example.material.pages.commons.ChatRoomScreen
import com.example.material.pages.commons.Importance
import com.example.material.pages.commons.Notice
import com.example.material.pages.commons.NoticesRoute
import com.example.material.pages.commons.NoticesScreen
import com.example.material.pages.commons.ProfileScreen
import com.example.material.pages.commons.SecurityScreen
import com.example.material.pages.commons.SettingsScreen
import com.example.material.pages.commons.StaticTextScreen
import com.example.material.pages.commons.TMGUpdateScreen
import com.example.material.pages.students.StuDest
import com.example.material.pages.students.StudentChatScreen
import com.example.material.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import navigateAndClearBackStack
import java.time.LocalDate

data class ClassNameResponse(val className: String)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TeacherNavHost(navController: NavHostController,
                   startDestination: Destination = Destination.HOME,
                   modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier,
        enterTransition = { defaultEnterTransition() },
        exitTransition = { defaultExitTransition() },
        popEnterTransition = { defaultPopEnterTransition() },
        popExitTransition = { defaultPopExitTransition() }
    ) {
        composable(Destination.HOME.route) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val appViewModel: AppViewModel = hiltViewModel()
            TeacherHomeContent(
                onStartClassClick = {
                    navController.navigate(Destination.START_CLASS.route)
                },
                onContinueClassClick = { ongoingClass ->
                    val encodedClassId = Uri.encode(ongoingClass.className)
                    if (ongoingClass.stage == "ATTENDANCE") {
                        navController.navigate(
                            Destination.ATTENDANCE_SCREEN.createRoute(encodedClassId)
                        )
                    } else {
                        navController.navigate(
                            Destination.ENDING_SCREEN.createRoute(encodedClassId)
                        )
                    }
                },
                onNotificationsClick = {
                    Log.d("NoticeFlow", "▶️  navController.navigate(notice)")
                    navController.navigate(Destination.notice.route)
                },
                onUpdateCheckClick = {
                    Log.d("UpdateCheck", "▶️  navController.navigate(update)")
                    navController.navigate(Destination.update.route)
                },

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
                },
                onResultsClick = {
                    navController.navigate(Destination.START_RESULTS.route)
                },
                onMarksClick = {
                    navController.navigate(Destination.marks.route)
                },
                onPtmClick = {
                    navController.navigate(Destination.ptm.route)
                },



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
        composable(Destination.NOTES.route) {
            NotesScreen(
                onAddNoteClick = { navController.navigate(Destination.sharenotes.route) }
            )
        }
        composable(Destination.ptm.route) {
            PtmScreen(
                onBack = { navController.popBackStack() },
                onRequestClick = {
                }
            )
        }

        composable(Destination.marks.route) {
            ResultListScreen(
                teacherName = "Teacher",
                onBack = { navController.popBackStack() },
            )
        }

        composable(Destination.Results.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("id") ?: ""
            ResultEntryScreen(
                classId = classId,
                onBack = { navController.popBackStack() }
            )
        }


        composable(Destination.SETTINGS.route) {

            SettingsScreen(
                onNavigateToStaticPage = { heading, content ->
                    navController.navigate(Destination.static_pages.createRoute(heading, content))
                },
                onMyAccClick = {  navController.navigate(Destination.profile.route) },
                onUpdateClick = { navController.navigate(Destination.update.route) },
                onSecurityClick = { navController.navigate(Destination.security.route) },
            )
        }

        composable(Destination.sharenotes.route) {

            UploadNotesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Destination.notice.route) {
            NoticesRoute(onBack = { navController.popBackStack() })
        }


        composable(Destination.START_RESULTS.route) {
            StartResultScreen(
                onBack = { navController.popBackStack() },
                onCreateClass = { id ->
                    navController.navigate(Destination.Results.createRoute(id))

                }
            )
        }




        composable(Destination.START_CLASS.route) {

            StartClassScreen(
                onBack = { navController.popBackStack() },
                onCreateClass = { id ->
                    navController.navigate(Destination.ATTENDANCE_SCREEN.createRoute(id))

                }
            )
        }
        composable(
            route = Destination.static_pages.route,
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

        composable(Destination.profile.route) {
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

        composable(StuDest.update.route) {
            TMGUpdateScreen(
                version = BuildConfig.VERSION_NAME,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Destination.security.route) {
            SecurityScreen(
                onBack = { navController.popBackStack() }
            )
        }


        composable(
            route = Destination.ATTENDANCE_SCREEN.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("id") ?: ""
            TakeAttendanceScreen(
                classId = classId,
                onBackClick = { navController.navigate(Destination.HOME.route) },
                onAttendanceSubmitted = {
                    navController.navigate(Destination.ENDING_SCREEN.createRoute(classId))
                }
            )
        }

        composable(
            route = Destination.ENDING_SCREEN.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("id") ?: ""
            EndClassScreen(
                classId = classId,
                onBackClick = { navController.navigate(Destination.HOME.route) }
            )
        }

        composable(
            Screen.Login.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            LoginScreen(
                onForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onLoginSuccess = { token, role ->
                    when (role) {
                        "STUDENT" -> navController.navigateAndClearBackStack(Screen.StudentHome.route)
                        "TEACHER" -> navController.navigateAndClearBackStack(Screen.TeacherHome.route)
                        "ADMIN" -> navController.navigateAndClearBackStack(Screen.AdminHome.route)
                    }
                }
            )
        }


    }
}



@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentTransitionScope<*>.defaultEnterTransition(): EnterTransition {
    return slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) +
            fadeIn(tween(300))
}

@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentTransitionScope<*>.defaultExitTransition(): ExitTransition {
    return slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) +
            fadeOut(tween(300))
}

@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentTransitionScope<*>.defaultPopEnterTransition(): EnterTransition {
    return slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) +
            fadeIn(tween(300))
}

@OptIn(ExperimentalAnimationApi::class)
private fun AnimatedContentTransitionScope<*>.defaultPopExitTransition(): ExitTransition {
    return slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) +
            fadeOut(tween(300))
}
