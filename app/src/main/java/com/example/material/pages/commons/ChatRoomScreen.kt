package com.example.material.pages.commons

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.R
import com.example.material.viewmodel.chat.ChatRoomViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class ChatMessage(
    val id: String,
    val senderUsername: String,
    val className: String ,
    val message: String,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.init(className, canEveryoneMessage, username)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            ChatInput(
                onSend = { viewModel.sendMessage(className, it) },
                focusRequester = focusRequester,
                canMessage = role == "TEACHER" || (role == "STUDENT" && canEveryoneMessage)
            )
        }
    ) { pad ->
        // The fix is here: Apply all padding to the parent Box.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .imePadding() // This is the key fix to handle keyboard overlap
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        keyboardController?.hide()
                    })
                }
        ) {
            LazyColumn(
                state = listState,
                // The LazyColumn no longer needs contentPadding or imePadding.
                // It just fills the space provided by the parent Box.
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = if (messages.isEmpty()) Arrangement.Center else Arrangement.Top
            ) {
                if (messages.isEmpty()) {
                    item {
                        EmptyChatPlaceholder()
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    MessageBubble(
                        message = msg,
                        isMe = msg.senderUsername == username || msg.senderUsername == "me",
                        onLongPress = {
                            Toast.makeText(context, "Long-press on message", Toast.LENGTH_SHORT).show()
                            // You can add a dropdown menu here for copy/delete options
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyChatPlaceholder() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ChatBubble,
            contentDescription = "No messages icon",
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No messages yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Start the conversation!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: ChatMessage,
    isMe: Boolean,
    onLongPress: () -> Unit
) {
    val backgroundColor = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val senderNameColor = if (isMe) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
    val timeColor = if (isMe) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    // 👇 Replace logic here
    val displayName = when (message.senderUsername) {
        "chiranjit@tmg.teacher" -> "TMG OFFICIAL"
        else -> message.senderUsername
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isMe) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                Text(
                    text = displayName.firstOrNull()?.toString()?.uppercase() ?: "?",
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMe) 16.dp else 4.dp,
                        bottomEnd = if (isMe) 4.dp else 16.dp
                    )
                )
                .background(backgroundColor)
                .combinedClickable(
                    onClick = { /* No-op for now */ },
                    onLongClick = onLongPress
                )
                .padding(10.dp)
        ) {
            AnimatedVisibility(visible = !isMe, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = displayName, // 👈 show processed name
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = senderNameColor,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Text(
                text = message.message,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = formatTime(message.timestamp),
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
                color = timeColor
            )
        }
    }
}


@Composable
fun ChatInput(
    onSend: (String) -> Unit,
    focusRequester: FocusRequester,
    canMessage: Boolean
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    AnimatedVisibility(
        visible = canMessage,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
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
                placeholder = { Text("Message...") },
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

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (text.text.isNotBlank()) {
                        onSend(text.text)
                        try {
                            val mediaPlayer = MediaPlayer.create(context, R.raw.sent)
                            mediaPlayer.setOnCompletionListener { it.release() }
                            mediaPlayer.start()
                        } catch (e: Exception) {
                            // Handle potential errors with the media player
                        }
                        text = TextFieldValue("")
                    }
                },
                enabled = text.text.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (text.text.isNotBlank()) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface
                    )
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (text.text.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }
    }
}

private fun formatTime(timestamp: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val time = OffsetDateTime.parse(timestamp, formatter)
        time.toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a"))
    } catch (e: Exception) {
        "Invalid time"
    }
}