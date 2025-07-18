package com.example.material.pages.admin.ATTENDANCE

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.material.api.AttendanceSummary
import com.example.material.viewmodel.attendance.AttendanceUiState
import com.example.material.viewmodel.attendance.AttendanceViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onBack: () -> Unit = {},
    onItemClick: (String) -> Unit = {},
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val refreshing = state is AttendanceUiState.Loading

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = refreshing),
            onRefresh = { viewModel.load() },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val result = state) {
                is AttendanceUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is AttendanceUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${result.message}", color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.load() }) { Text("Retry") }
                        }
                    }
                }

                is AttendanceUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        result.grouped.forEach { (date, items) ->
                            item {
                                Text(date, style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            items(items) { item ->
                                AttendanceCard(item, onClick = { onItemClick(item.attendanceId) })
                            }
                        }
                        item {
                            Spacer(Modifier.height(24.dp))
                            Text(
                                "You have reached the end.",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceCard(item: AttendanceSummary, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = { Text("${item.className}") },
            supportingContent = {
                Column {
                    Text("By: ${item.teacherUsername}")
                }
            },
            trailingContent = {
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        )
    }
}