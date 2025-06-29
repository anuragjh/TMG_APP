package com.example.material.pages.admin.USERS

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.R
import com.example.material.api.UserProfileUpdateRequest
import com.example.material.viewmodel.UserDetailsUiState
import com.example.material.viewmodel.UserDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsScreen(
    username: String,
    onBack: () -> Unit = {},
    onComplete: () -> Unit = {},
    onClassDetailsClick: (String) -> Unit = {},
    viewModel: UserDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    LaunchedEffect(username) {
        viewModel.fetchUserDetails(username)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is UserDetailsUiState.Loading -> LoadingIndicator()

                is UserDetailsUiState.Error -> ErrorContent(state.message) {
                    viewModel.fetchUserDetails(username)
                }

                is UserDetailsUiState.Success -> {
                    val user = state.data
                    var name by remember { mutableStateOf(user.name) }
                    var phone by remember { mutableStateOf(user.phone) }
                    var gmail by remember { mutableStateOf(user.gmail) }

                    val isModified by remember(name, phone, gmail) {
                        mutableStateOf(name != user.name || phone != user.phone || gmail != user.gmail)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(140.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = user.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = gmail,
                                onValueChange = { gmail = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        Toast.makeText(
                                            context,
                                            "Username cannot be changed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            ) {
                                OutlinedTextField(
                                    value = user.username,
                                    onValueChange = {},
                                    label = { Text("Username") },
                                    readOnly = true,
                                    enabled = false,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text("Classes Joined", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(12.dp))

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                user.classes.forEach { className ->
                                    ElevatedCard(
                                        onClick = { onClassDetailsClick(className) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        ListItem(
                                            headlineContent = { Text(className) },
                                            trailingContent = {
                                                Icon(
                                                    Icons.Default.ArrowForward,
                                                    contentDescription = "Go"
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Bottom buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    val req = UserProfileUpdateRequest(name, gmail, phone)
                                    viewModel.updateUser(username, req) { success, message ->
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                                },
                                enabled = isModified
                            ) {
                                Text("Update")
                            }

                            OutlinedButton(
                                onClick = { showDialog = true },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("DELETE")
                            }
                        }

                        // Delete Confirmation Dialog
                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showDialog = false
                                            isDeleting = true
                                            viewModel.deleteUser(
                                                username = username,
                                                onSuccess = {
                                                    isDeleting = false
                                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                                    onComplete()
                                                },
                                                onFailure = {
                                                    isDeleting = false
                                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        }
                                    ) {
                                        Text("Yes")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDialog = false }) {
                                        Text("No")
                                    }
                                },
                                title = { Text("Delete User") },
                                text = { Text("Are you sure you want to delete ${user.username}?") }
                            )
                        }

                        // Deleting Progress Dialog
                        if (isDeleting) {
                            AlertDialog(
                                onDismissRequest = {},
                                confirmButton = {},
                                title = { Text("Deleting...") },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                        Text("Please wait")
                                    }
                                }
                            )
                        }
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(12.dp))
        Text("Loading user details...")
    }
}

@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Error: $message", color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
