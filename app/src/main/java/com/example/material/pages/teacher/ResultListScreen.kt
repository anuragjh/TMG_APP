package com.example.material.pages.teacher

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.viewmodel.teacher.TeacherResultsViewModel
import com.example.material.viewmodel.teacher.ResultsListUiState
import com.example.material.viewmodel.teacher.ResultData
import com.example.material.viewmodel.teacher.ResultStudentPayload
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultListScreen(
    teacherName: String,
    onBack: () -> Unit,
    viewModel: TeacherResultsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(teacherName) {
        viewModel.loadTeacherResults()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Marksheets", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ResultsListUiState.Loading -> {
                    // Centering the loading indicator
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center)
                    )
                }
                is ResultsListUiState.Error -> {
                    Text(
                        "Error: ${state.message}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ResultsListUiState.Success -> {
                    if (state.results.isEmpty()) {
                        Text(
                            "No results found for this teacher.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.results) { result ->
                                ResultCard(result = result)
                            }
                        }
                    }
                }
                else -> { /* Initial state */ }
            }
        }
    }
}


@Composable
fun ResultCard(result: ResultData) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "rotation")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }, // Clicking the whole row toggles the expansion
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.testName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Class: ${result.className} | Total Marks: ${result.totalMarks}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse",
                    modifier = Modifier.rotate(rotation), // Animate the rotation
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(8.dp))

                // Headers for the student list
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Rank", modifier = Modifier.weight(0.2f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text("Student Name", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text("Marks", modifier = Modifier.weight(0.3f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) { // Use LazyColumn for long lists
                    items(result.students) { student ->
                        StudentResultRow(student = student)
                    }
                }
            }
        }
    }
}

@Composable
fun StudentResultRow(student: ResultStudentPayload) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(student.rank, modifier = Modifier.weight(0.2f), style = MaterialTheme.typography.bodyMedium)
            Text(student.studentName, modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.bodyMedium)
            Text("${student.marksObtained}", modifier = Modifier.weight(0.3f), style = MaterialTheme.typography.bodyMedium, textAlign = androidx.compose.ui.text.style.TextAlign.End)
        }
    }
}