package com.example.material.pages.admin.CLASS


import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.viewmodel.ClassFormViewModel
import com.example.material.viewmodel.UserViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun TeacherSelectionScreen(
    onBack: () -> Unit,
    onUpdate: () -> Unit,
    viewModel: UserViewModel = hiltViewModel(),
    formViewModel: ClassFormViewModel = hiltViewModel()
) {
    val teachers by viewModel.users.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    var selected by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        viewModel.loadUsers("TEACHER")
    }

    SwipeRefreshLayout(
        isRefreshing = isLoading,
        onRefresh = { viewModel.loadUsers("TEACHER") }
    ) {
        UserSelectionScreen(
            title = "Select Teachers",
            users = teachers,
            selectedUsers = selected,
            onToggleSelect = {
                selected = selected.toMutableSet().apply {
                    if (contains(it)) remove(it) else add(it)
                }
            },
            onBack = onBack,
            onUpdate = {
                formViewModel.setSelectedTeachers(selected.toList())
                onUpdate()
            }
        )
    }
}



@Composable
fun SwipeRefreshLayout(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh
    ) {
        content()
    }
}