package com.example.material.pages.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.material.ui.theme.TMGTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onSettingClick: () -> Unit = {},
    onNavigateToClass: () -> Unit = {},
    onNavigateToUser: () -> Unit = {},
    onNavigateToAttendance: () -> Unit = {},
    onNavigateToChatroom: () -> Unit = {},
    onNavigateToNotice: () -> Unit = {},
    onNavigateToNotes: () -> Unit = {}
) {
    val config = LocalConfiguration.current
    val isLandscape = config.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Welcome Admin,",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "controls",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSettingClick,
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "Settings" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxSize()

        if (isLandscape) {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardListItem("Class Management", Icons.Default.School, onNavigateToClass)
                    CardListItem("User Management", Icons.Default.Group, onNavigateToUser)
                    CardListItem("Attendance Management", Icons.Default.CheckCircle, onNavigateToAttendance)
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardListItem("Chatroom Management", Icons.Default.Chat, onNavigateToChatroom)
                    CardListItem("Notice", Icons.Default.Notifications, onNavigateToNotice)
                    CardListItem("Notes Management", Icons.Default.Note, onNavigateToNotes)
                }
            }
        } else {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CardListItem("Class Management", Icons.Default.School, onNavigateToClass)
                CardListItem("User Management", Icons.Default.Group, onNavigateToUser)
                CardListItem("Attendance Management", Icons.Default.CheckCircle, onNavigateToAttendance)
                CardListItem("Chatroom Management", Icons.Default.Chat, onNavigateToChatroom)
                CardListItem("Notice", Icons.Default.Notifications, onNavigateToNotice)
                CardListItem("Notes Management", Icons.Default.Note, onNavigateToNotes)
            }
        }
    }
}

@Composable
fun CardListItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            leadingContent = {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingContent = {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Go to $title",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}