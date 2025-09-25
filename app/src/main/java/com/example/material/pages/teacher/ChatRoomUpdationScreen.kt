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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.api.NonUserResponse
import com.example.material.viewmodel.teacher.ChatUpdationViewModel
import com.example.material.viewmodel.teacher.UserListUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ChatRoomUpdationScreen(
    className: String,
    viewModel: ChatUpdationViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val chatUpdateResult by viewModel.chatUpdateResult.collectAsState()

    var selectedPermission by remember { mutableStateOf("Everyone can Chat") }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var isUpdating by remember { mutableStateOf(false) }

    val selectedUsernamesFromViewModel by viewModel.selectedUsernames.collectAsState()
    val currentUsername by viewModel.currentUsername.collectAsState()

    // A local mutable state map to manage user selection
    val selectedUsersMap = remember { mutableStateMapOf<String, Boolean>() }

    // Sync local selectedUsersMap with data from ViewModel and current user
    LaunchedEffect(selectedUsernamesFromViewModel, currentUsername) {
        selectedUsersMap.clear()
        selectedUsernamesFromViewModel.forEach { username ->
            selectedUsersMap[username] = true
        }
        // Also ensure the current user is ticked by default
        currentUsername?.let {
            selectedUsersMap[it] = true
        }
    }

    // Load data when the screen is first composed
    LaunchedEffect(Unit) {
        viewModel.loadUsername(context)
        viewModel.loadUsersForClass(className)
    }

    // Handle update result
    LaunchedEffect(chatUpdateResult) {
        chatUpdateResult?.let { result ->
            isUpdating = false
            result.onSuccess {
                Toast.makeText(context, "Chatroom updated successfully", Toast.LENGTH_SHORT).show()
                onBack()
            }.onFailure {
                val message = it.message ?: "Something went wrong"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
            viewModel.resetUpdateResult() // Important: reset the state
        }
    }

    val isUpdateButtonEnabled = selectedUsersMap.any { it.value }

    // Progress dialog
    if (isUpdating) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Updating Chatroom") },
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
                title = { Text(className, style = MaterialTheme.typography.titleLarge) },
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
                                onClick = { selectedPermission = "Everyone can Chat" }
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
                                onClick = { selectedPermission = "Only Teachers can chat" }
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
                    if (isUpdating) return@Button

                    val allUsersInClass = selectedUsersMap.filterValues { it }.keys.toMutableSet()

                    currentUsername?.let { allUsersInClass.add(it) }

                    val students = allUsersInClass.filter { username ->
                        uiState.users.firstOrNull { it.username == username }?.role.equals("STUDENT", ignoreCase = true)
                    }
                    val teachers = allUsersInClass.filter { username ->
                        uiState.users.firstOrNull { it.username == username }?.role.equals("TEACHER", ignoreCase = true)
                    }

                    isUpdating = true
                    viewModel.updateChatRoom(
                        className = className,
                        students = students.toList(),
                        teachers = teachers.toList(),
                        everyone = selectedPermission == "Everyone can Chat"
                    )
                },
                enabled = isUpdateButtonEnabled && !isUpdating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp)
            ) {
                Text("Update ChatRoom", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}