package com.example.material.pages.teacher

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.viewmodel.teacher.ClassListUiState
import com.example.material.viewmodel.teacher.StartClassViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartResultScreen(
    onBack: () -> Unit,
    onCreateClass: (String) -> Unit,
    viewModel: StartClassViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    // Load classes once on screen launch
    LaunchedEffect(Unit) {
        viewModel.loadClasses()
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
                                onClick = {
                                    onCreateClass(classItem.className) // Directly navigate
                                },
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
        }
    }
}
