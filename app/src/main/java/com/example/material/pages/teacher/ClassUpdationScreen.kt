package com.example.material.pages.teacher

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.viewmodel.teacher.ChatRoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassUpdationScreen(
    viewModel: ChatRoomViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    navController: androidx.navigation.NavController
) {
    val classIds by viewModel.chatRoomIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    var expandedMenuForClass by remember { mutableStateOf<String?>(null) }
    var showDeleteDialogForClass by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Show a toast message when the toastMessage state changes
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.toastMessageShown()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchMyCreatedChatRooms()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Class Updation", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                classIds.isEmpty() -> {
                    Text(
                        text = "No chatrooms created.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(classIds, key = { it }) { className ->
                            ClassListItem(
                                className = className,
                                onClassClick = {
                                    navController.navigate(Destination.ChatRoomUpdation.withArgs(className))
                                },
                                onMoreClick = { expandedMenuForClass = className },
                                isMenuExpanded = expandedMenuForClass == className,
                                onDismissMenu = { expandedMenuForClass = null },
                                onDeleteOptionClick = {
                                    showDeleteDialogForClass = className
                                    expandedMenuForClass = null
                                }
                            )
                        }
                    }
                }
            }

            showDeleteDialogForClass?.let { className ->
                AlertDialog(
                    onDismissRequest = { showDeleteDialogForClass = null },
                    title = { Text("Delete Class Chatroom") },
                    text = { Text("Are you sure you want to delete '$className'?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                // Call the ViewModel function to delete the class
                                viewModel.deleteChatRoom(className)
                                showDeleteDialogForClass = null
                            },
                            enabled = !isLoading
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialogForClass = null }) {
                            Text("No")
                        }
                    }
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassListItem(
    className: String,
    onClassClick: (String) -> Unit,
    onMoreClick: () -> Unit,
    isMenuExpanded: Boolean,
    onDismissMenu: () -> Unit,
    onDeleteOptionClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClassClick(className) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = className,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onMoreClick) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = onDismissMenu
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete Class Chatroom") },
                        onClick = onDeleteOptionClick
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go to class",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
