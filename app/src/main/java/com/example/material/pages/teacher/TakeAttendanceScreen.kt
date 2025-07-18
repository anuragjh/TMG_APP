package com.example.material.pages.teacher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.viewmodel.teacher.AttendanceUiState
import com.example.material.viewmodel.teacher.TakeAttendanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakeAttendanceScreen(
    classId: String,
    viewModel: TakeAttendanceViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onAttendanceSubmitted: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    val attendanceStates = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(Unit) {
        viewModel.loadStudents(classId)
    }

    LaunchedEffect(uiState) {
        if (uiState is AttendanceUiState.Submitted) {
            Toast.makeText(context, "Attendance submitted successfully", Toast.LENGTH_SHORT).show()
            onAttendanceSubmitted(classId)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    viewModel.submitAttendance(classId, attendanceStates)
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("No") }
            },
            title = { Text("Confirm Attendance") },
            text = { Text("Are you sure you want to submit attendance?") }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Take Attendance") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            bottomBar = {
                if (uiState is AttendanceUiState.Success) {
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Complete")
                    }
                }
            }
        ) { paddingValues ->
            when (uiState) {
                is AttendanceUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is AttendanceUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (uiState as AttendanceUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is AttendanceUiState.Success -> {
                    val students = (uiState as AttendanceUiState.Success).students

                    students.forEach { student ->
                        if (student.username !in attendanceStates) {
                            attendanceStates[student.username] = true
                        }
                    }

                    LazyColumn(
                        contentPadding = paddingValues,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(students) { student ->
                            val isPresent = attendanceStates[student.username] == true

                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = student.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Row {
                                        Button(
                                            onClick = { attendanceStates[student.username] = true },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isPresent)
                                                    MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                                contentColor = if (isPresent)
                                                    MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                        ) { Text("P") }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Button(
                                            onClick = { attendanceStates[student.username] = false },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (!isPresent)
                                                    MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surface,
                                                contentColor = if (!isPresent)
                                                    MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurface
                                            )
                                        ) { Text("A") }
                                    }
                                }
                            }
                        }
                    }
                }

                AttendanceUiState.Submitted -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> {}
            }
        }
    }
}


