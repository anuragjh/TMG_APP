package com.example.material.pages.teacher

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.material.R
import com.example.material.api.OngoingClass
import com.example.material.datastore.DataStoreManager
import com.example.material.viewmodel.teacher.TeacherHomeUiState
import com.example.material.viewmodel.teacher.TeacherHomeViewModel
import com.google.accompanist.swiperefresh.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Dummy icons for a richer drawer
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.ExitToApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherHomeContent(
    viewModel: TeacherHomeViewModel = hiltViewModel(),
    onStartClassClick: () -> Unit = {},
    onContinueClassClick: (OngoingClass) -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onResultsClick: () -> Unit = {},
    onPtmClick: () -> Unit = {},
    onUpdateCheckClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onMarksClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("") }
    val ctx = LocalContext.current
    val dataStore = remember { DataStoreManager(ctx) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadClasses()
        val cachedUsername = withContext(Dispatchers.IO) {
            dataStore.getUsername()
        }

        if (cachedUsername != null) {
            username = cachedUsername
            Log.d("LoginScreen", "✅ Username loaded from cache: $cachedUsername")
        } else {
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

            val finalUsername = fetchedUsername ?: "Unknown User"
            username = finalUsername

            dataStore.saveUsername(finalUsername)
            Log.d("LoginScreen", "✅ Username fetched from API and saved: $finalUsername")
        }
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
    val displayName = username.trim().substringBefore("@").replaceFirstChar { it.uppercaseChar() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerShape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 0.dp)
            ) {
                // Customized Drawer Header with a gradient background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                                color =   MaterialTheme.colorScheme.secondaryContainer
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        AsyncImage(
                            model = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSB7RWP-ntuHhdxDONcT9QCkUuQd9bXLgoYSQ&s",
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = displayName,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = username
                        )
                    }
                }

                // Navigation items with more expressive icons and colors
                Spacer(Modifier.height(8.dp))
                NavigationDrawerItem(
                    label = { Text("Notifications") },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Notifications") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNotificationsClick()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Upload Results") },
                    icon = { Icon(Icons.Default.Grade, contentDescription = "Results") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onResultsClick()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                NavigationDrawerItem(
                    label = { Text("View Marks") },
                    icon = { Icon(Icons.Default.StickyNote2, contentDescription = "View Results") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onMarksClick()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Request PTM") },
                    icon = { Icon(Icons.Default.Group, contentDescription = "PTM") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onPtmClick()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Divider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Check for Updates") },
                    icon = { Icon(Icons.Default.SystemUpdate, contentDescription = "Updates") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onUpdateCheckClick()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    label = { Text("Logout") },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout") },
                    selected = false,
                    onClick = { onLogoutClick() },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(16.dp))
            }
        },
    ) {
        // Main Screen Content
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { viewModel.refreshClasses() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Hello $displayName,",
                                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Light, color = MaterialTheme.colorScheme.onBackground)
                            )
                        }
                        Row {
                            IconButton(onClick = onNotificationsClick) {
                                Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", modifier = Modifier.size(28.dp))
                            }
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Outlined.Menu, contentDescription = "Open navigation menu", modifier = Modifier.size(28.dp))
                            }
                        }
                    }
                }

                item {
                    ElevatedCard(
                        onClick = onStartClassClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Start a class",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Start a new class",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                            )
                        }
                    }
                }

                item {
                    Text(
                        "Your Ongoing Classes",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                when (uiState) {
                    is TeacherHomeUiState.Loading -> {
                        if (classes.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(200.dp),
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
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 48.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Filled.School,
                                        contentDescription = "No classes found",
                                        modifier = Modifier.size(72.dp),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Text(
                                        text = "No ongoing classes at the moment.\nReady to start a new one?",
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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
}


@Composable
fun OngoingClassCard(
    ongoingClass: OngoingClass,
    onClick: () -> Unit
) {
    val formattedClassName = remember(ongoingClass.className) {
        ongoingClass.className.substringBefore("DATE")
    }

    val cardColor = remember {
        val colors = listOf(
            Color(0xFFE3F2FD), // Light Blue
            Color(0xFFFFFDE7), // Light Yellow
            Color(0xFFF3E5F5)  // Light Purple
        )
        colors.random()
    }

    // Get the default color outside the remember block
    val defaultOnSurfaceColor = MaterialTheme.colorScheme.onSurface

    val contentColor = remember(cardColor) {
        when (cardColor) {
            Color(0xFFE3F2FD) -> Color(0xFF1976D2)
            Color(0xFFFFFDE7) -> Color(0xFFF9A825)
            Color(0xFFF3E5F5) -> Color(0xFF8E24AA)
            else -> defaultOnSurfaceColor // Use the pre-calculated value here
        }
    }

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .animateContentSize(animationSpec = spring()),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = formattedClassName,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (ongoingClass.stage) {
                        "ATTENDANCE" -> "Take Attendance"
                        else -> "Continue Class"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Go to class",
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}