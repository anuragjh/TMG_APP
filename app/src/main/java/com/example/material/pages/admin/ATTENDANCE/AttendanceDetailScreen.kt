package com.example.material.pages.admin.ATTENDANCE

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.api.AttendanceDetail
import com.example.material.api.TimestampData
import com.example.material.viewmodel.AttendanceDetailViewModel
import java.text.SimpleDateFormat
import java.util.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceDetailScreen(
    detail: AttendanceDetail,
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val list = if (selectedTab == 0) detail.absent else detail.present

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = detail.className,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text("By: ${detail.teacherUsername}")
            Text("On : ${formatDate(detail.date)}")
            Text("Time: ${detail.startTime} - ${detail.endTime}")
            Text("Topic Covered: ${detail.topicCovered}")

            Spacer(Modifier.height(16.dp))

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("ABSENT") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("PRESENT") }
                )
            }

            Spacer(Modifier.height(12.dp))

            if (list.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No users in this category.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(list) { username ->
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ListItem(
                                headlineContent = { Text(username) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: TimestampData): String {
    val date = Date(timestamp.seconds * 1000)
    return SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
}
@Composable
fun AttendanceDetailScreenWrapper(
    id: String,
    viewModel: AttendanceDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val loading by viewModel.loading.collectAsState()
    val detail by viewModel.detail.collectAsState()

    LaunchedEffect(id) {
        viewModel.load(id)
    }

    when {
        loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        detail != null -> {
            AttendanceDetailScreen(detail = detail!!, onBack = onBack)
        }
        else -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Failed to load attendance detail")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.load(id) }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
