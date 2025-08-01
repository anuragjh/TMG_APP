package com.example.material.pages.students

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.api.ApiService
import com.example.material.api.AttendanceList
import com.example.material.viewmodel.student.AttendanceUiState
import com.example.material.viewmodel.student.StudentViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Data class for AttendanceList ---
// This is needed because you're passing a List<ApiService.AttendanceEntry>
// into a ViewModel function, and Compose's navigation or ViewModel
// might require it to be wrapped in a Parcelable or Serializable class
// if it were to be passed as an argument. For simple direct usage here,
// a data class is sufficient.

// --- Global Composable Functions ---
// Moved these composable functions outside of any other composable
// to make them accessible throughout the file and avoid "Unresolved reference" errors.

@Composable
private fun CircularActionButton(
    icon: ImageVector,
    contentDesc: String,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDesc,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
fun CompactInfoCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    iconTint: Color,
    backgroundColor: Color,
    onCardClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.clickable { onCardClick() }
    ) {
        Column(
            Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun RoutineCard(currentDay: String, onRoutineClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRoutineClick() }
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = "Routine",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Daily Routine",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Today is $currentDay",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(
                    onClick = { onRoutineClick() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("View Full Routine", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun FeesCard(onFeeClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFeeClick() }
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccountBalanceWallet,
                    contentDescription = "Fees",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Fees Status",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(16.dp))
            if (true) {
                Text(
                    "4 months due",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Please settle your outstanding balance.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    "No outstanding fees",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Your fees are up to date. Keep up the good work!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(
                    onClick = { onFeeClick() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("View Payment History", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun SemiCircularDualProgressIndicator(
    presentPercentage: Float,
    absentPercentage: Float,
    presentColor: Color,
    absentColor: Color,
    trackColor: Color,
    strokeWidth: Dp,
    modifier: Modifier = Modifier
) {
    val animatedPresentProgress by animateFloatAsState(
        targetValue = presentPercentage,
        animationSpec = tween(durationMillis = 1000, delayMillis = 200)
    )
    val animatedAbsentProgress by animateFloatAsState(
        targetValue = absentPercentage,
        animationSpec = tween(durationMillis = 1000, delayMillis = 200)
    )

    Canvas(modifier = modifier) {
        // Draw the full semi-circle track background
        drawArc(
            color = trackColor,
            startAngle = 180f, // Start from the left horizontal
            sweepAngle = 180f, // Sweep 180 degrees for a semi-circle
            useCenter = false,
            style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
        )

        // Draw the Present progress arc
        drawArc(
            color = presentColor,
            startAngle = 180f, // Start from the left horizontal
            sweepAngle = animatedPresentProgress * 180f, // Animate sweep based on percentage
            useCenter = false,
            style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
        )

        // Draw the Absent progress arc
        // It starts immediately after the Present arc ends
        drawArc(
            color = absentColor,
            startAngle = 180f + (animatedPresentProgress * 180f), // Start where present arc ends (180 degrees max sweep for semi-circle)
            sweepAngle = animatedAbsentProgress * 180f, // Animate sweep based on percentage
            useCenter = false,
            style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentReportScreen(
    onBellClick: () -> Unit = {},
    onPollClick: () -> Unit = {},
    onRoutineClick: () -> Unit = {},
    onResultClick : () -> Unit = {},
    onPTMClick: () -> Unit = {},
    onFeeClick: () -> Unit = {},
) {
    val currentDay = remember { SimpleDateFormat("EEEE", Locale.getDefault()).format(Date()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        "Hey Student!",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Let's make today productive!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.weight(1f))
                CircularActionButton(icon = Icons.Default.BarChart, contentDesc = "Polls", onClick = onPollClick)
                Spacer(Modifier.width(16.dp))
                CircularActionButton(icon = Icons.Default.Notifications, contentDesc = "Notifications", onClick = onBellClick)
            }
        }

        item {
            AttendanceCard()
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                CompactInfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Results",
                    icon = Icons.Default.School,
                    iconTint = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    onCardClick = {onResultClick()}
                )
                CompactInfoCard(
                    modifier = Modifier.weight(1f),
                    title = "PTMs",
                    icon = Icons.Default.Diversity3,
                    iconTint = MaterialTheme.colorScheme.tertiary,
                    backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                    onCardClick = {onPTMClick()}
                )
            }
        }

        item {
            RoutineCard(currentDay, onRoutineClick = onRoutineClick)
        }

        item {
            FeesCard(onFeeClick = onFeeClick)
        }

        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "More sections coming soon!",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun AttendanceCard(
    vm: StudentViewModel = hiltViewModel(),
    onDetailsClick: () -> Unit = {}
) {
    val state by vm.uiState.collectAsState()

    when (state) {
        is AttendanceUiState.Loading -> LoadingAttendanceCard()
        is AttendanceUiState.Success -> {
            val data = (state as AttendanceUiState.Success).data
            val t = data.attendance
            Log.d("AttendanceCard", "Attendance data: ${t}")

            val present = data.attendedClasses
            val total = data.totalClasses

            // In your Composable
            val context = LocalContext.current
            val attendanceList = AttendanceList(data.attendance) // data is MyAttendanceResponse

            StylishAttendanceCard(
                present = data.attendedClasses,
                total = data.totalClasses,
                onDetailsClick = {
//
                },
                attendanceList = data.attendance
            )


        }
        is AttendanceUiState.Error -> Text("Error: ${(state as AttendanceUiState.Error).message}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingAttendanceCard() {
    Card(shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(6.dp)) {
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(4.dp)))
                Spacer(Modifier.width(12.dp))
                Box(modifier = Modifier.height(20.dp).fillMaxWidth(0.6f).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(4.dp)))
            }
            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(120.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(40.dp, 20.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), RoundedCornerShape(4.dp)))
                }
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.height(20.dp).fillMaxWidth(0.4f).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(4.dp)))
            }

            Spacer(Modifier.height(24.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(40.dp, 20.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(4.dp)))
                        Spacer(Modifier.height(4.dp))
                        Box(modifier = Modifier.height(16.dp).fillMaxWidth(0.2f).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(4.dp)))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Box(modifier = Modifier.size(120.dp, 40.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(8.dp)))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StylishAttendanceCard(
    present: Int,
    total: Int,
    onDetailsClick: () -> Unit,
    vm: StudentViewModel = hiltViewModel(),
    attendanceList: List<ApiService.AttendanceEntry> = emptyList()
) {

    val presentPercentage = if (total > 0) present / total.toFloat() else 0f
    val absentPercentage = if (total > 0) (total - present) / total.toFloat() else 0f
    val displayPercentage = (presentPercentage * 100).toInt()
    var showDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Average Attendance",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(120.dp), // This Box defines the drawing area
                    contentAlignment = Alignment.Center
                ) {
                    // This is where the progress indicator is drawn
                    SemiCircularDualProgressIndicator(
                        presentPercentage = presentPercentage,
                        absentPercentage = absentPercentage,
                        presentColor = MaterialTheme.colorScheme.primary,
                        absentColor = MaterialTheme.colorScheme.error,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 12.dp,
                        modifier = Modifier.fillMaxSize() // Canvas should fill its parent Box
                    )
                    Text(
                        text = "$displayPercentage%",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Attendance Achieved",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$total",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Total Classes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$present",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Present",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${total - present}",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        "Absent",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {


                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Text("Send Report to Mail", style = MaterialTheme.typography.labelLarge)
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Send Attendance Report?") },
                        text = { Text("Do you want to receive the attendance report on your mail?") },
                        confirmButton = {
                            TextButton(onClick = {
                                showDialog = false
                                scope.launch {
                                    vm.viewFullReportAndShowMessage(
                                        AttendanceList(attendanceList)
                                    ) { message ->
                                        Toast.makeText(context, message ?: "No message", Toast.LENGTH_LONG).show()
                                    }
                                }
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


            }
        }
    }
}


@Composable
fun AttendanceLegend(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}