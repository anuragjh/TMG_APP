/*──────────────────────── 1) DESTINATIONS ────────────────────────*/
package com.example.material.pages.students

import android.net.Uri
import androidx.annotation.Keep
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

@Keep
sealed class StuDest(open val route: String) {

    sealed class BottomNav(
        override val route: String,
        val icon: ImageVector,
        val label: String,
        val contentDescription: String
    ) : StuDest(route)


    data object NOTES    : BottomNav("notes"   ,  Icons.Default.Notes     , "Notes"   , "Notes screen")
    data object HOME   : BottomNav("reports" , Icons.Default.Home  , "Home" , "Home screen")
    data object CHATS    : BottomNav("chats"   , Icons.Default.Chat       , "Chats"   , "Chats screen")
    data object SETTINGS : BottomNav("more"    , Icons.Default.MoreHoriz  , "More"    , "Settings")

    data object static_pages : StuDest("static_pages/{heading}/{content}") {
        fun createRoute(heading: String, content: String): String {
            return "static_pages/${Uri.encode(heading)}/${Uri.encode(content)}"
        }
    }

    data object profile : StuDest("profile")

    data object notices : StuDest("notices")

    data object polls : StuDest("polls")

    data object security : StuDest("security")

    data object update : StuDest("update")

//    data object chatRoom : StuDest("chat_room/{classname}") {
//        fun createRoute(classname: String): String {
//            return "chat_room/${Uri.encode(classname)}"
//        }
//    }



        data object chatRoom : StuDest("chat_room/{className}/{canEveryoneMessage}/{username}") {
            fun withArgs(className: String, canEveryoneMessage: Boolean,username : String?): String {
                return "chat_room/$className/$canEveryoneMessage/$username"
            }
        }


    companion object {
        val bottomNavItems = listOf(HOME, NOTES, CHATS, SETTINGS)
    }
}

