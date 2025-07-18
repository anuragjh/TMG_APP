package com.example.material.pages.teacher

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.api.OngoingClass
import com.example.material.viewmodel.teacher.TeacherHomeUiState
import com.example.material.viewmodel.teacher.TeacherHomeViewModel
import com.google.accompanist.swiperefresh.*
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.platform.LocalContext
import com.example.material.datastore.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherHomeContent(
    viewModel: TeacherHomeViewModel = hiltViewModel(),
    onStartClassClick: () -> Unit = {},
    onContinueClassClick: (OngoingClass) -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("") }
    val ctx       = LocalContext.current
    val dataStore = remember { DataStoreManager(ctx) }

    LaunchedEffect(Unit) {
        viewModel.loadClasses()

        val fetchedUsername = withContext(Dispatchers.IO) {
            runCatching {
                val response = viewModel.getUsernameFromApi()
                if (response?.isSuccessful == true) {
                    response.body()?.string()?.trim()
                } else {
                    Log.e("TeacherHomeScreen", "❌ Error fetching username: ${response?.code()}")
                    null
                }
            }.getOrNull()
        }

        username = fetchedUsername ?: "Unknown User"
        dataStore.saveUsername(username)
        Log.d("LoginScreen", "✅ Username fetched: $username")
    }



    val classes = when (uiState) {
        is TeacherHomeUiState.Success -> (uiState as TeacherHomeUiState.Success).classes
        is TeacherHomeUiState.Loading -> {
            val existing = viewModel.uiState.value
            if (existing is TeacherHomeUiState.Success) existing.classes else emptyList()
        }
        else -> emptyList()
    }

    val isRefreshing = uiState is TeacherHomeUiState.Loading && classes.isNotEmpty()

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { viewModel.refreshClasses() }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            "Hello Teacher",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    },
                    supportingContent = {
                        Text(
                            "Start or wrap up classes",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    trailingContent = {
                        IconButton(
                            onClick = { onNotificationsClick()},
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector      = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint             = MaterialTheme.colorScheme.primary,
                                modifier         = Modifier.size(28.dp)
                            )
                        }
                    }
                )
            }



            item {
                ElevatedCard(
                    onClick = onStartClassClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Start a class",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start a class",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            when (uiState) {
                is TeacherHomeUiState.Loading -> {
                    if (classes.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    } else {
                        items(classes) { ongoingClass ->
                            OngoingClassCard(
                                ongoingClass = ongoingClass,
                                onClick = { onContinueClassClick(ongoingClass) }
                            )
                        }
                    }
                }

                is TeacherHomeUiState.Success -> {
                    if (classes.isEmpty()) {
                        item {
                            Text(
                                text = "No ongoing classes found.",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        items(classes) { ongoingClass ->
                            OngoingClassCard(
                                ongoingClass = ongoingClass,
                                onClick = { onContinueClassClick(ongoingClass) }
                            )
                        }
                    }
                }


                is TeacherHomeUiState.Error -> {
                    item {
                        Text(
                            text = "Error: ${(uiState as TeacherHomeUiState.Error).message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
fun OngoingClassCard(
    ongoingClass: OngoingClass,
    onClick: () -> Unit
) {
    val formattedClassName = remember(ongoingClass.className) {
        ongoingClass.className.substringBefore("DATE")
    }

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = formattedClassName,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (ongoingClass.stage) {
                        "ATTENDANCE" -> "Take Attendance"
                        else -> "Continue Class"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Go to class"
            )
        }
    }
}
