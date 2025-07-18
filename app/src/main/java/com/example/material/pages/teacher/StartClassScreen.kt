package com.example.material.pages.teacher

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.viewmodel.teacher.ClassListUiState
import com.example.material.viewmodel.teacher.StartClassViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartClassScreen(
    onBack: () -> Unit,
    onCreateClass: (String) -> Unit,
    viewModel: StartClassViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState
    val startStatus by viewModel.startStatus
    val isStarting by viewModel.isStarting
    var selectedClassName by remember { mutableStateOf<String?>(null) }

    // Trigger load when screen starts
    LaunchedEffect(Unit) {
        viewModel.loadClasses()
    }

    // Handle toast for successful start
    LaunchedEffect(startStatus) {
        startStatus?.let { status ->
            if (!status.startsWith("ERROR")) {
                Toast.makeText(context, "Class Started: $status", Toast.LENGTH_LONG).show()
                onCreateClass(status)
            } else {
                Toast.makeText(context, status, Toast.LENGTH_LONG).show()
            }
            viewModel.clearStartStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Classes") },
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
            when (uiState) {
                is ClassListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ClassListUiState.Error -> {
                    val msg = (uiState as ClassListUiState.Error).message
                    Text(
                        text = msg,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is ClassListUiState.Success -> {
                    val classList = (uiState as ClassListUiState.Success).classes
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(classList) { classItem ->
                            ElevatedCard(
                                onClick = { selectedClassName = classItem.className },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ListItem(
                                    headlineContent = { Text(classItem.className) },
                                    trailingContent = {
                                        Icon(Icons.Default.ArrowForward, contentDescription = "Go")
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Confirmation Dialog
            selectedClassName?.let { name ->
                AlertDialog(
                    onDismissRequest = { selectedClassName = null },
                    title = { Text("Create Class") },
                    text = { Text("Do you want to create class: $name?") },
                    confirmButton = {
                        TextButton(onClick = {
                            selectedClassName = null
                            viewModel.startClass(name) // API Call
                        }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { selectedClassName = null }) {
                            Text("No")
                        }
                    }
                )
            }

            // Loading Dialog during API
            if (isStarting) {
                AlertDialog(
                    onDismissRequest = { },
                    confirmButton = {},
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Starting class...")
                        }
                    }
                )
            }
        }
    }
}
