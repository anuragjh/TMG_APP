package com.example.material.pages.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.viewmodel.teacher.EndClassUiState
import com.example.material.viewmodel.teacher.EndClassViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndClassScreen(
    onBackClick: () -> Unit = {},
    classId: String,
    viewModel: EndClassViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var topicText by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    // When Success → show toast and go back
    LaunchedEffect(uiState) {
        when (uiState) {
            is EndClassUiState.Success -> {
                Toast.makeText(context, "Class ended", Toast.LENGTH_SHORT).show()
                viewModel.reset()
                onBackClick()
            }
            is EndClassUiState.Error -> {
                Toast.makeText(context, (uiState as EndClassUiState.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.reset()
            }
            else -> {}
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("End Class?") },
            text = { Text("Are you sure you want to end the class?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    viewModel.endClass(classId, topicText)
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Topic Covered") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = topicText,
                        onValueChange = {
                            topicText = it
                            if (showError) showError = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        label = { Text("Topic Covered / Remarks") },
                        placeholder = { Text("Enter what was taught in this class...") },
                        isError = showError,
                        singleLine = false,
                        maxLines = 10
                    )
                    if (showError) {
                        Text(
                            text = "Please enter topic covered",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (topicText.trim().isEmpty()) {
                            showError = true
                        } else {
                            showDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        "End Class",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            if (uiState is EndClassUiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}


