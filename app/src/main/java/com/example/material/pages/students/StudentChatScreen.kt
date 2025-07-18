package com.example.material.pages.students

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.R
import com.example.material.api.ChatRoomResponse
import com.example.material.viewmodel.chat.ChatViewModel
import com.google.accompanist.swiperefresh.*
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class ChatPreview(
    val sender: String,
    val message: String,
    val date: String,
    val initials: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onChatClick: (ChatRoomResponse) -> Unit = {}
) {
    val chatRooms by viewModel.chatRooms.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = loading)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Messages",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Profile click */ }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { pad ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.fetchChatRooms() },
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            when {
                loading && chatRooms.isEmpty() -> {
                    // Show loading indicator when first loading
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                chatRooms.isEmpty() -> {
                    // No chatrooms
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.chat),
                            contentDescription = "No chats",
                            modifier = Modifier
                                .size(220.dp)
                                .padding(bottom = 24.dp)
                        )
                        Text("Not added in any group yet")
                    }
                }

                else -> {
                    LazyColumn {
                        items(chatRooms) { room ->
                            val preview = room.toChatPreview()
                            ChatRow(chat = preview) {
                                onChatClick(room)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatRow(chat: ChatPreview, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = chat.initials,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.sender,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = chat.message,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = chat.date,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun ChatRoomResponse.toChatPreview(): ChatPreview {
    val last = this.lastMessage
    val senderName = last?.senderUsername?.substringBefore("@") ?: "No messages"
    val initials = className.firstOrNull()?.toString() ?: "#"
    val message = last?.message ?: "No messages yet"
    val timestamp = last?.timestamp?.let {
        try {
            OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("dd MMM • hh:mm a"))
        } catch (e: Exception) {
            "Unknown"
        }
    } ?: "No date"
    return ChatPreview(
        sender = className,
        message = "$senderName: $message",
        date = timestamp,
        initials = initials.uppercase()
    )
}