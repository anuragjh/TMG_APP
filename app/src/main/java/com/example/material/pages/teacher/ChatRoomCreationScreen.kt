package com.example.material.pages.teacher

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.viewmodel.teacher.ChatCreationViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ChatRoomCreationScreen(
    viewModel: ChatCreationViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val chatCreateResult by viewModel.chatCreateResult.collectAsState()

    var chatroomName by remember { mutableStateOf(TextFieldValue("")) }
    var selectedPermission by remember { mutableStateOf("Everyone can Chat") }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var isCreating by remember { mutableStateOf(false) }

    val selectedUsersMap = remember { mutableStateMapOf<String, Boolean>() }

    val isNameValid = chatroomName.text.length >= 6 && chatroomName.text.none { it in listOf('\\', '/', '|') }
    val isCreateButtonEnabled = isNameValid && selectedUsersMap.any { it.value }

    val currentUsername by viewModel.currentUsername.collectAsState()

    // Load current user
    LaunchedEffect(Unit) {
        viewModel.loadUsername(context)
    }

    // Auto-select and lock current user
    LaunchedEffect(currentUsername, uiState.users) {
        currentUsername?.let { username ->
            if (uiState.users.any { it.username == username }) {
                selectedUsersMap[username] = true
            }
        }
    }

    // Handle creation result
    LaunchedEffect(chatCreateResult) {
        chatCreateResult?.let { result ->
            isCreating = false
            result.onSuccess {
                selectedUsersMap.clear()
                chatroomName = TextFieldValue("")
                searchText = TextFieldValue("")
                selectedPermission = "Everyone can Chat"
                Toast.makeText(context, "Chatroom created successfully", Toast.LENGTH_SHORT).show()
                onBack()
            }.onFailure {
                val message = it.message ?: "Something went wrong"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Progress dialog
    if (isCreating) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Creating Chatroom") },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Please wait...")
                }
            },
            confirmButton = {}
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ChatRoom Creation", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = chatroomName,
                    onValueChange = { chatroomName = it },
                    label = { Text("Chatroom Name") },
                    isError = !isNameValid && chatroomName.text.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                AnimatedVisibility(
                    visible = !isNameValid && chatroomName.text.isNotEmpty(),
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Text(
                        text = "Name must be at least 6 characters and cannot contain \\, / or |",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Who can chat?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (selectedPermission == "Everyone can Chat"),
                                    onClick = { selectedPermission = "Everyone can Chat" }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedPermission == "Everyone can Chat"),
                                onClick = null
                            )
                            Text(
                                text = "Everyone can Chat",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (selectedPermission == "Only Teachers can chat"),
                                    onClick = { selectedPermission = "Only Teachers can chat" }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedPermission == "Only Teachers can chat"),
                                onClick = null
                            )
                            Text(
                                text = "Only Teachers can chat",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Add Users",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Search") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                ) {
                    AnimatedContent(targetState = uiState.isLoading, label = "") { isLoading ->
                        when {
                            isLoading -> {
                                Box(Modifier.fillMaxSize(), Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }

                            uiState.error != null -> {
                                Box(Modifier.fillMaxSize(), Alignment.Center) {
                                    Text(uiState.error ?: "Failed to load users")
                                }
                            }

                            else -> {
                                val filteredUsers = uiState.users.filter {
                                    it.name.contains(searchText.text, ignoreCase = true)
                                }

                                if (filteredUsers.isEmpty()) {
                                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                                        Text("No users found.")
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(filteredUsers, key = { it.username }) { user ->
                                            val isCurrentUser = user.username == currentUsername
                                            val isSelected = selectedUsersMap[user.username] ?: false

                                            ListItem(
                                                headlineContent = { Text(user.name, fontWeight = FontWeight.Medium) },
                                                supportingContent = { Text(user.role) },
                                                trailingContent = {
                                                    Checkbox(
                                                        checked = isSelected,
                                                        onCheckedChange = {
                                                            if (!isCurrentUser) {
                                                                selectedUsersMap[user.username] = it
                                                            }
                                                        },
                                                        enabled = !isCurrentUser
                                                    )
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(MaterialTheme.shapes.medium)
                                                    .clickable(enabled = !isCurrentUser) {
                                                        selectedUsersMap[user.username] = !isSelected
                                                    }
                                                    .padding(horizontal = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (isCreating) return@Button

                    val selectedUsernames = selectedUsersMap.filterValues { it }.keys
                    val selectedUsers = uiState.users.filter { it.username in selectedUsernames }.toMutableList()

                    // Ensure current user is included
                    currentUsername?.let { username ->
                        if (selectedUsers.none { it.username == username }) {
                            uiState.users.find { it.username == username }?.let { selectedUsers.add(it) }
                        }
                    }

                    val students = selectedUsers.filter { it.role.equals("STUDENT", ignoreCase = true) }.map { it.username }
                    val teachers = selectedUsers.filter { it.role.equals("TEACHER", ignoreCase = true) }.map { it.username }.toMutableList()

                    // Force current user into teachers list
                    currentUsername?.let {
                        if (!teachers.contains(it)) {
                            teachers.add(it)
                        }
                    }

                    isCreating = true
                    viewModel.createChatRoom(
                        className = chatroomName.text,
                        students = students,
                        teachers = teachers,
                        everyone = selectedPermission == "Everyone can Chat"
                    )
                },
                enabled = isCreateButtonEnabled && !isCreating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp)
            ) {
                Text("Create ChatRoom", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
