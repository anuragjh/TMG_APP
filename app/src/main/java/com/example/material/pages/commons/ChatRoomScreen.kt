package com.example.material.pages.commons

import android.media.MediaPlayer

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import java.time.*
import java.time.format.DateTimeFormatter


import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.R
import com.example.material.viewmodel.chat.ChatRoomViewModel


data class ChatMessage(
    val id: String,
    val senderUsername: String,
    val className: String ,
    val message: String,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    className: String,
    canEveryoneMessage: Boolean,
    username: String,
    onBack: () -> Unit,
    viewModel: ChatRoomViewModel = hiltViewModel()
) {
    val role by viewModel.role.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.init(className, canEveryoneMessage, username)
    }

    // Auto-scroll to bottom when new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Auto-focus text field when screen loads if user can message
    LaunchedEffect(role) {
        if (role == "TEACHER" || (role == "STUDENT" && canEveryoneMessage)) {
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(className) },
                navigationIcon = {
                    IconButton(onClick = {
                        keyboardController?.hide()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            when {
                role == null -> Box(modifier = Modifier.padding(16.dp)) {
                    Text("Loading...", style = MaterialTheme.typography.bodySmall)
                }

                role == "TEACHER" -> ChatInput(
                    onSend = { viewModel.sendMessage(className, it) },
                    focusRequester = focusRequester
                )

                role == "STUDENT" && canEveryoneMessage -> ChatInput(
                    onSend = { viewModel.sendMessage(className, it) },
                    focusRequester = focusRequester
                )

                role == "STUDENT" -> Box(Modifier.padding(16.dp)) {
                    Text("Students can't send messages")
                }
            }
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        keyboardController?.hide()
                    })
                }
        ) {
            LazyColumn(
                state = listState,
                contentPadding = pad,
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                verticalArrangement = if (messages.isEmpty()) Arrangement.Center else Arrangement.Top
            ) {
                if (messages.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No messages yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Send your first message!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                }

                items(messages, key = { it.id }) { msg -> // No longer reversed
                    val isMe = msg.senderUsername == username || msg.senderUsername == "me"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            modifier = Modifier
                                .background(
                                    color = if (isMe) Color(0xFF1976D2) else Color(0xFFF1F0F0),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(10.dp)
                        ) {
                            if (!isMe) {
                                Text(
                                    msg.senderUsername,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )
                            }

                            Text(
                                msg.message ?: "[empty]",
                                color = if (isMe) Color.White else Color.Black
                            )



                            Text(
                                formatTime(msg.timestamp),
                                fontSize = 10.sp,
                                modifier = Modifier.align(Alignment.End),
                                color = if (isMe) Color.White.copy(alpha = 0.7f) else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInput(
    onSend: (String) -> Unit,
    focusRequester: FocusRequester
) {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .navigationBarsPadding()
            .imePadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Message") },
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        IconButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSend(text)
                    try {
                        val mediaPlayer = MediaPlayer.create(context, R.raw.sent)
                        mediaPlayer.setOnCompletionListener { it.release() }
                        mediaPlayer.start()
                    } catch (e: Exception) {

                    }
                    text = ""
                }
            },
            enabled = text.isNotBlank()
        ) {
            Icon(
                Icons.Default.Send,
                contentDescription = "Send",
                tint = if (text.isNotBlank()) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}


private fun formatTime(timestamp: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val time = OffsetDateTime.parse(timestamp, formatter)
        time.toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm a"))
    } catch (e: Exception) {
        "Invalid time"
    }
}