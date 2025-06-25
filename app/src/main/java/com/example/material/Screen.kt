package com.example.material

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.material.pages.admin.AdminHomeScreen
import com.example.material.pages.admin.ClassesCreationScreen
import com.example.material.pages.admin.ClassesManagmentScreen
import com.example.material.pages.auth.ForgotPasswordScreen
import com.example.material.pages.auth.LoginScreen
import com.example.material.pages.auth.OTPVerifyScreen
import com.example.material.pages.auth.UpdatePasswordScreen

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
    object ClassCreation : Screen("class_creation")
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
fun AppNavGraph(startDestination: String = Screen.Login.route) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onLoginSuccess = { token, role ->
                    when (role) {
                        "STUDENT" -> navController.navigateAndClearBackStack(Screen.StudentHome.route)
                        "TEACHER" -> navController.navigateAndClearBackStack(Screen.TeacherHome.route)
                        "ADMIN" -> navController.navigateAndClearBackStack(Screen.AdminHome.route)
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }


        composable(Screen.StudentHome.route) {
            Text("Student Home Screen")
        }

        composable(Screen.TeacherHome.route) {
            Text("Teacher Home Screen")
        }

        composable(Screen.AdminHome.route) {
            AdminHomeScreen(onNavigateToClass = {
                navController.navigate(Screen.ClassManagement.route)
            },)
        }
        composable(
            route = Screen.OtpVerify.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OTPVerifyScreen(email = email, navController = navController)
        }

        composable(
            route = Screen.ChangePassword.route,
            arguments = listOf(navArgument("key") { type = NavType.StringType })
        ) { backStackEntry ->
            val key = backStackEntry.arguments?.getString("key") ?: ""
            UpdatePasswordScreen(key = key, navController = navController)
        }


        composable(Screen.ClassManagement.route) {
            ClassesManagmentScreen(
                onCreateClass = { navController.navigate(Screen.ClassCreation.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ClassCreation.route) {
            ClassesCreationScreen(
                onBack = { navController.popBackStack() }
            )
        }



    }
}
