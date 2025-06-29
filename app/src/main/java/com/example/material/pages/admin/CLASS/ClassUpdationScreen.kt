package com.example.material.pages.admin.CLASS

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.api.ClassNameResponse
import com.example.material.viewmodel.ClassViewModel
import com.google.accompanist.swiperefresh.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassUpdationScreen(
    viewModel: ClassViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onClassClick: (String) -> Unit
) {
    val classes by viewModel.classes.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchClassList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Class Updation") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isLoading),
            onRefresh = { viewModel.fetchClassList() },
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(classes) { classItem ->
                    ClassCardItem(classItem, onClick = { onClassClick(classItem.className) })
                }
            }
        }
    }
}

@Composable
fun ClassCardItem(classItem: ClassNameResponse, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = { Text(classItem.className) },
            trailingContent = {
                Icon(Icons.Default.ArrowForward, contentDescription = "Go")
            }
        )
    }
}

