// ResultEntryScreen.kt
package com.example.material.pages.teacher

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.viewmodel.teacher.ResultEntryUiState
import com.example.material.viewmodel.teacher.ResultEntryViewModel
import com.example.material.viewmodel.teacher.StudentInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultEntryScreen(
    classId: String,
    onBack: () -> Unit = {},
    viewModel: ResultEntryViewModel = hiltViewModel()
) {
    var testName by remember { mutableStateOf("Test") }
    var totalMarks by remember { mutableStateOf("60") }
    var expandedStudentUsername by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsState()
    val studentsInput by viewModel.studentsInput.collectAsState()
    val context = LocalContext.current


    var showDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.loadStudents(classId)
    }

    LaunchedEffect(uiState) {
        if (uiState is ResultEntryUiState.Submitted) {
            Toast.makeText(context, "Results submitted successfully!", Toast.LENGTH_SHORT).show()
            onBack()
        } else if (uiState is ResultEntryUiState.Error) {
            Toast.makeText(context, (uiState as ResultEntryUiState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    val isSubmitButtonEnabled by remember {
        derivedStateOf {
            val isMainFormValid = testName.isNotBlank() && totalMarks.isNotBlank()
                    && totalMarks.toFloatOrNull() != null && totalMarks.toFloat() > 0
            val areAllStudentsCompleted = studentsInput.all { it.isCompleted }
            isMainFormValid && areAllStudentsCompleted
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    viewModel.submitResults(
                        testName = testName,
                        totalMarks = totalMarks.toIntOrNull() ?: 0
                        , classId = classId
                    )
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("No") }
            },
            title = { Text("Confirm Result Entry") },
            text = { Text("Are you sure you want to submit these results?") }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "$classId") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is ResultEntryUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ResultEntryUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        (uiState as ResultEntryUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is ResultEntryUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = testName,
                            onValueChange = { testName = it },
                            label = { Text("Test Name") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = totalMarks,
                            onValueChange = { totalMarks = it },
                            label = { Text("Total Marks") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(studentsInput, key = { it.username }) { student ->
                            StudentItem(
                                student = student,
                                totalMarks = totalMarks.toFloatOrNull(),
                                isExpanded = expandedStudentUsername == student.username,
                                onToggleExpand = {
                                    expandedStudentUsername = if (expandedStudentUsername == student.username) null else student.username
                                },
                                onMarksChange = { marks ->
                                    viewModel.updateStudentMarks(student, marks, totalMarks.toFloatOrNull())
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showDialog = true },
                        enabled = isSubmitButtonEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(text = "Submit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            ResultEntryUiState.Submitted -> {
                // You can add a success indicator here if needed
            }
        }
    }
}

@Composable
fun StudentItem(
    student: StudentInput,
    totalMarks: Float?,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onMarksChange: (String) -> Unit
) {
    val marksFloat = student.marksObtained.toFloatOrNull()
    val isMarksInvalid = marksFloat != null && totalMarks != null && (marksFloat > totalMarks || marksFloat < 0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(durationMillis = 300)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val statusIcon = when {
                    student.isCompleted -> Icons.Default.CheckCircle
                    isMarksInvalid -> Icons.Default.Info
                    else -> Icons.Default.Close
                }
                val statusTint = when {
                    student.isCompleted -> MaterialTheme.colorScheme.primary
                    isMarksInvalid -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.error
                }

                Icon(
                    imageVector = statusIcon,
                    contentDescription = "Status",
                    tint = statusTint,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                }

                IconButton(onClick = onToggleExpand) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand/Collapse",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(top = 0.dp)
                ) {
                    OutlinedTextField(
                        value = student.marksObtained,
                        onValueChange = onMarksChange,
                        label = { Text("Marks Obtained") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = isMarksInvalid,
                        trailingIcon = {
                            if (isMarksInvalid) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Invalid marks",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                    if (isMarksInvalid) {
                        Text(
                            text = "Marks must be between 0 and $totalMarks",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ResultEntryScreenPreview() {
    ResultEntryScreen(classId = "Class 9 - Computer")
}