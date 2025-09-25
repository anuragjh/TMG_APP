package com.example.material.pages.admin

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.viewmodel.admin.HealthUiState
import com.example.material.viewmodel.admin.HealthViewModel
import com.google.gson.annotations.SerializedName
import java.time.Duration
import java.time.format.DateTimeParseException


data class HealthMetrics(
    @SerializedName("serverStatus") val serverStatus: String?,
    @SerializedName("uptime") val uptime: String?,
    @SerializedName("cpu") val cpu: CpuMetrics?,
    @SerializedName("ram") val ram: MemoryMetrics?,
    @SerializedName("storage") val storage: StorageMetrics?,
    @SerializedName("mongoDb") val mongoDb: MongoDbMetrics?
)


data class CpuMetrics(
    @SerializedName("systemCpuLoad") val systemCpuLoad: Double?,
    @SerializedName("availableProcessors") val availableProcessors: Int?
)

data class MemoryMetrics(
    @SerializedName("totalMemoryBytes") val totalMemoryBytes: Long?,
    @SerializedName("freeMemoryBytes") val freeMemoryBytes: Long?,
    @SerializedName("usedMemoryBytes") val usedMemoryBytes: Long?
)

data class StorageMetrics(
    @SerializedName("totalSpaceBytes")val totalSpaceBytes: Long?,
    @SerializedName("usableSpaceBytes") val usableSpaceBytes: Long?,
    @SerializedName("freeSpaceBytes") val freeSpaceBytes: Long?
)

data class MongoDbMetrics(
    @SerializedName("status") val status: String?,
    @SerializedName("version") val version: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen(
    onBack: () -> Unit = {},
    viewModel: HealthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Server Health Metrics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.fetchHealthMetrics() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                ErrorScreen(message = uiState.error ?: "Unknown error") {
                    viewModel.fetchHealthMetrics()
                }
            }
            uiState.metrics != null -> {
                val metrics = uiState.metrics
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { metrics?.let { ServerStatusCard(it) } ?: PlaceholderCard("Server status unavailable") }
                    item { metrics?.cpu?.let { CpuMetricsCard(it) } ?: PlaceholderCard("CPU metrics unavailable") }
                    item { metrics?.ram?.let { RamMetricsCard(it) } ?: PlaceholderCard("RAM metrics unavailable") }
                    item { metrics?.storage?.let { StorageMetricsCard(it) } ?: PlaceholderCard("Storage metrics unavailable") }
                    item { metrics?.mongoDb?.let { MongoDbMetricsCard(it) } ?: PlaceholderCard("MongoDB metrics unavailable") }
                }
            }
            else -> {
                PlaceholderCard("No health data available")
            }
        }
    }
}

@Composable
fun PlaceholderCard(message: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(message, color = Color.Gray)
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: $message",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun ServerStatusCard(metrics: HealthMetrics) {
    val isRunning = metrics.serverStatus?.equals("running", ignoreCase = true) == true
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = "Status Icon",
                    tint = if (isRunning) Color.Green else Color.Red
                )
                Spacer(Modifier.width(8.dp))
                Text("Server Status", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text("Status: ${metrics.serverStatus ?: "Unknown"}")
            Text("Uptime: ${formatUptimeFromISOString(metrics.uptime)}")
        }
    }
}

private fun formatUptimeFromISOString(uptimeString: String?): String {
    if (uptimeString.isNullOrBlank()) return "N/A"
    return try {
        val duration = Duration.parse(uptimeString)
        val seconds = duration.seconds
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        "%d h, %d min, %d sec".format(hours, minutes, secs)
    } catch (_: DateTimeParseException) {
        "N/A"
    }
}

@Composable
fun CpuMetricsCard(cpuMetrics: CpuMetrics) {
    val load = cpuMetrics.systemCpuLoad ?: 0.0
    val progress by animateFloatAsState(load.toFloat(), animationSpec = tween(1000), label = "")
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("CPU Metrics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("System Load: %.2f%%".format(load * 100))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (progress > 0.8f) Color.Red else Color.Green
            )
            Spacer(Modifier.height(8.dp))
            Text("Available Processors: ${cpuMetrics.availableProcessors ?: "N/A"}")
        }
    }
}

@Composable
fun RamMetricsCard(ramMetrics: MemoryMetrics) {
    val total = ramMetrics.totalMemoryBytes ?: 0L
    val used = ramMetrics.usedMemoryBytes ?: 0L
    val free = ramMetrics.freeMemoryBytes ?: 0L
    val usage = if (total > 0) used.toFloat() / total.toFloat() else 0f
    val progress by animateFloatAsState(usage, animationSpec = tween(1000), label = "")

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("RAM Metrics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Used: ${formatBytesToGB(used)} of ${formatBytesToGB(total)}")
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (progress > 0.8f) Color.Red else Color.Green
            )
            Spacer(Modifier.height(8.dp))
            Text("Free: ${formatBytesToGB(free)}")
        }
    }
}

@Composable
fun StorageMetricsCard(storageMetrics: StorageMetrics) {
    val total = storageMetrics.totalSpaceBytes ?: 0L
    val usable = storageMetrics.usableSpaceBytes ?: 0L
    val free = storageMetrics.freeSpaceBytes ?: 0L

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Storage Metrics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Total Space: ${formatBytesToGB(total)}")
            Text("Used Space: ${formatBytesToGB(total - usable)}")
            Text("Free Space: ${formatBytesToGB(free)}")
        }
    }
}

@Composable
fun MongoDbMetricsCard(mongoDbMetrics: MongoDbMetrics) {
    val isConnected = mongoDbMetrics.status?.equals("connected", ignoreCase = true) == true
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isConnected) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = "Status Icon",
                    tint = if (isConnected) Color.Green else Color.Red
                )
                Spacer(Modifier.width(8.dp))
                Text("Database Status", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text("Status: ${mongoDbMetrics.status ?: "Unknown"}")
            Text("Version: ${mongoDbMetrics.version ?: "N/A"}")
        }
    }
}

private fun formatBytesToGB(bytes: Long): String {
    val gb = bytes / (1024.0 * 1024.0 * 1024.0)
    return "%.5f GB".format(gb)
}
