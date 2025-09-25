package com.example.material.pages.teacher

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.material.pages.students.StuDest



sealed class Destination(open val route: String) {

    sealed class BottomNavDestination(
        override val route: String,
        val icon: ImageVector,
        val label: String,
        val contentDescription: String
    ) : Destination(route)

    data object HOME : BottomNavDestination("home", Icons.Default.Home, "Home", "Home screen")
    data object CHATS : BottomNavDestination("chats", Icons.Default.Chat, "Chats", "Chat screen")
    data object NOTES : BottomNavDestination("notes", Icons.Default.Note, "Notes", "Notes screen")
    data object SETTINGS : BottomNavDestination("more", Icons.Default.MoreHoriz, "More", "Settings screen")

    data object START_CLASS : Destination("start_class")
    data object START_RESULTS : Destination("start_results")
    data object ATTENDANCE_SCREEN : Destination("attendance/{id}") {
        fun createRoute(id: String) = "attendance/$id"
    }
    data object Results : Destination("result/{id}") {
        fun createRoute(id: String) = "result/$id"
    }

    data object ENDING_SCREEN : Destination("endclass/{id}") {
        fun createRoute(id: String) = "endclass/$id"
    }

    data object ChatRoomUpdation : Destination("chat_room_updation/{className}") {
        fun withArgs(className: String): String {
            return "chat_room_updation/$className"
        }
    }

    data object CallingScreen: Destination("calling/{username}") {
        fun withArgs(username: String): String {
            return "calling/$username"
        }
    }

    data class StaticPageArgs(val heading: String, val content: String)

    data object static_pages : Destination("static_pages/{heading}/{content}") {
        fun createRoute(heading: String, content: String): String {
            return "static_pages/${Uri.encode(heading)}/${Uri.encode(content)}"
        }
    }
    data object profile : Destination("profile")

    data object update : Destination("update")

    data object security : Destination("security")

    data object sharenotes : Destination("sharenotes")

    data object notice : Destination("notices")

    data object chatRoom : Destination("chat_room/{className}/{canEveryoneMessage}") {
        fun withArgs(className: String, canEveryoneMessage: Boolean): String {
            return "chat_room/$className/$canEveryoneMessage"
        }
    }

    data object chatCreate : Destination("chat_create")
    data object chatUpdationManagment : Destination("chat_updation") {
    }
    data object chatManagment : Destination("chat_management")

    data object marks : Destination("marks")

    data object ptm : Destination("ptm")




    companion object {
        val bottomNavItems = listOf(HOME, CHATS, NOTES, SETTINGS)
    }
}
