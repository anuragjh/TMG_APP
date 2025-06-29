import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.material.pages.admin.CLASS.AddUsersScreen
import com.example.material.pages.admin.AdminHomeScreen
import com.example.material.pages.admin.CLASS.ClassDetailsScreen
import com.example.material.pages.admin.CLASS.ClassUpdationScreen
import com.example.material.pages.admin.CLASS.ClassUserMnagementScreen
import com.example.material.pages.admin.CLASS.ClassesCreationScreen
import com.example.material.pages.admin.CLASS.ClassesManagmentScreen
import com.example.material.pages.admin.CLASS.RemoveUsersScreen
import com.example.material.pages.admin.CLASS.StudentSelectionScreen
import com.example.material.pages.admin.CLASS.TeacherSelectionScreen
import com.example.material.pages.admin.USERS.CreateUserScreen
import com.example.material.pages.admin.USERS.UserDetailsScreen
import com.example.material.pages.admin.USERS.UserUpdationScreen
import com.example.material.pages.admin.USERS.UsersManagmentScreen
import com.example.material.pages.auth.ForgotPasswordScreen
import com.example.material.pages.auth.LoginScreen
import com.example.material.pages.auth.OTPVerifyScreen
import com.example.material.pages.auth.UpdatePasswordScreen
import com.example.material.viewmodel.ClassFormViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")
    object StudentHome : Screen("student_home")
    object TeacherHome : Screen("teacher_home")
    object AdminHome : Screen("admin_home")
    object OtpVerify : Screen("otp_verify/{email}") {
        fun createRoute(email: String) = "otp_verify/$email"
    }
    object ChangePassword : Screen("change_password/{key}") {
        fun createRoute(key: String) = "change_password/$key"
    }
    object ClassManagement : Screen("class_management")
    object UserManagement : Screen("user_management")
    object UserCreation : Screen("user_creation")
    object UserUpdation : Screen("user_updation")
    object UserDetails : Screen("user_details/{username}") {
        fun createRoute(username: String) = "user_details/$username"
    }
    object ClassCreation : Screen("class_creation")
    object StudentSelection : Screen("student_selection")
    object TeacherSelection : Screen("teacher_selection")
    object ClassUserManagment : Screen("class_user_management")
    object ClassUserManagement : Screen("class_user_management/{className}") {
        fun createRoute(className: String) = "class_user_management/$className"
    }
    object ClassUserRemove : Screen("class_user_remove/{className}") {
        fun createRoute(className: String) = "class_user_remove/$className"
    }
    object ClassUserAdd : Screen("class_user_add/{className}") {
        fun createRoute(className: String) = "class_user_add/$className"
    }
    object ClassDetails : Screen("class_details/{className}") {
        fun createRoute(className: String) = "class_details/$className"
    }
}

fun NavController.navigateAndClearBackStack(destination: String) {
    navigate(destination) {
        popUpTo(graph.startDestinationId) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

@Composable
inline fun <reified T : ViewModel> sharedViewModel(navBackStackEntry: NavBackStackEntry): T {
    return hiltViewModel(navBackStackEntry)
}

@Composable
fun AppNavGraph(startDestination: String = Screen.Login.route) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { defaultEnterTransition() },
        exitTransition = { defaultExitTransition() },
        popEnterTransition = { defaultPopEnterTransition() },
        popExitTransition = { defaultPopExitTransition() }
    ) {
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

        composable(
            Screen.ForgotPassword.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            ForgotPasswordScreen(navController = navController)
        }

        composable(
            Screen.StudentHome.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            Text("Student Home Screen")
        }

        composable(
            Screen.TeacherHome.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            Text("Teacher Home Screen")
        }

        composable(
            Screen.AdminHome.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            AdminHomeScreen(
                onNavigateToClass = {
                    navController.navigate(Screen.ClassManagement.route)
                },
                onNavigateToUser = {
                    navController.navigate(Screen.UserManagement.route)
                },
            )
        }

        composable(
            route = Screen.OtpVerify.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType }),
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OTPVerifyScreen(email = email, navController = navController)
        }

        composable(
            route = Screen.ChangePassword.route,
            arguments = listOf(navArgument("key") { type = NavType.StringType }),
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) { backStackEntry ->
            val key = backStackEntry.arguments?.getString("key") ?: ""
            UpdatePasswordScreen(key = key, navController = navController)
        }

        composable(
            Screen.ClassManagement.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            ClassesManagmentScreen(
                onCreateClass = { navController.navigate(Screen.ClassCreation.route) },
                onUpdateClass = {
                    navController.navigate(Screen.ClassUserManagment.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Screen.UserManagement.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            UsersManagmentScreen(
                onCreateUser = { navController.navigate(Screen.UserCreation.route) },
                onUpdateUser = {
                    navController.navigate(Screen.UserUpdation.route)
                },
                onBack = { navController.popBackStack() }
            )
        }



        //inner routes

        //user routes
        composable(
            Screen.UserCreation.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            val formViewModel = hiltViewModel<ClassFormViewModel>()
            CreateUserScreen(
                onBack = { navController.popBackStack() },
                onUserCreated = {
                    navController.navigateAndClearBackStack(Screen.AdminHome.route)
                }
            )
        }
        composable(
            Screen.UserUpdation.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            UserUpdationScreen(
                onBack = { navController.popBackStack() },
                onUserClick = { userName ->
                    navController.navigate(Screen.UserDetails.createRoute(userName))
                }
            )
        }

        composable(
            route = Screen.UserDetails.route,
            arguments = listOf(navArgument("username") { type = NavType.StringType }),
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("username") ?: ""
            UserDetailsScreen(
                username = userName,
                onBack = { navController.popBackStack() },
                onClassDetailsClick = { className ->
                    navController.navigate(Screen.ClassDetails.createRoute(className))
                },
                onComplete = {
                    navController.navigateAndClearBackStack(Screen.AdminHome.route)
                }
            )
        }


        // Class routes

        composable(
            Screen.ClassCreation.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            val formViewModel = hiltViewModel<ClassFormViewModel>()
            ClassesCreationScreen(
                formViewModel = formViewModel,
                onStudentTap = { navController.navigate(Screen.StudentSelection.route) },
                onTeacherTap = { navController.navigate(Screen.TeacherSelection.route) },
                onBack = { navController.popBackStack() },
                onClassCreated = {
                    navController.navigateAndClearBackStack(Screen.AdminHome.route)
                }
            )
        }

        composable(
            Screen.ClassUserManagment.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            ClassUpdationScreen(
                onBack = { navController.popBackStack() },
                onClassClick = { className ->
                    navController.navigate(Screen.ClassUserManagement.createRoute(className))
                }
            )
        }

        composable(
            route = Screen.ClassUserManagement.route,
            arguments = listOf(navArgument("className") { type = NavType.StringType }),
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) { backStackEntry ->
            val className = backStackEntry.arguments?.getString("className") ?: ""
            ClassUserMnagementScreen(
                onBack = { navController.popBackStack() },
                className = className,
                onClassAddClick = { className ->
                    navController.navigate(Screen.ClassUserAdd.createRoute(className))
                },
                onClassRemoveClick = { className ->
                    navController.navigate(Screen.ClassUserRemove.createRoute(className))
                },
                onClassDetailsClick = { className ->
                    navController.navigate(Screen.ClassDetails.createRoute(className))
                }
            )
        }

        composable(
            route = Screen.ClassDetails.route,
            arguments = listOf(navArgument("className") { type = NavType.StringType }),
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) { backStackEntry ->
            val className = backStackEntry.arguments?.getString("className") ?: ""
            ClassDetailsScreen(
                className = className,
                onBack = { navController.popBackStack() },
                onComplete = {
                    navController.navigateAndClearBackStack(Screen.AdminHome.route)
                }
            )
        }

        composable(
            route = Screen.ClassUserAdd.route,
            arguments = listOf(navArgument("className") { type = NavType.StringType }),
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) { backStackEntry ->
            val className = backStackEntry.arguments?.getString("className") ?: ""
            AddUsersScreen(
                className = className,
                onBack = { navController.popBackStack() },
                onComplete = {
                    navController.navigateAndClearBackStack(Screen.AdminHome.route)
                }
            )
        }

        composable(
            route = Screen.ClassUserRemove.route,
            arguments = listOf(navArgument("className") { type = NavType.StringType }),
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) { backStackEntry ->
            val className = backStackEntry.arguments?.getString("className") ?: ""
            RemoveUsersScreen(
                className = className,
                onBack = { navController.popBackStack() },
                onComplete = {
                    navController.navigateAndClearBackStack(Screen.AdminHome.route)
                }
            )
        }

        composable(
            Screen.StudentSelection.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) { entry ->
            val parentEntry = remember(navController) {
                navController.getBackStackEntry(Screen.ClassCreation.route)
            }
            val formViewModel = sharedViewModel<ClassFormViewModel>(parentEntry)
            StudentSelectionScreen(
                onBack = { navController.popBackStack() },
                onUpdate = { navController.popBackStack() },
                formViewModel = formViewModel
            )
        }

        composable(
            Screen.TeacherSelection.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) { entry ->
            val parentEntry = remember(navController) {
                navController.getBackStackEntry(Screen.ClassCreation.route)
            }
            val formViewModel = sharedViewModel<ClassFormViewModel>(parentEntry)
            TeacherSelectionScreen(
                onBack = { navController.popBackStack() },
                onUpdate = { navController.popBackStack() },
                formViewModel = formViewModel
            )
        }
    }
}

private fun AnimatedContentTransitionScope<*>.defaultEnterTransition(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(300))
}

private fun AnimatedContentTransitionScope<*>.defaultExitTransition(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(300)
    ) + fadeOut(animationSpec = tween(300))
}

private fun AnimatedContentTransitionScope<*>.defaultPopEnterTransition(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(300))
}

private fun AnimatedContentTransitionScope<*>.defaultPopExitTransition(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(300)
    ) + fadeOut(animationSpec = tween(300))
}