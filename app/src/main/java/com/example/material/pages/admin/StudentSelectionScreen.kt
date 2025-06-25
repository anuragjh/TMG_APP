package com.example.material.pages.admin

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.viewmodel.ClassFormViewModel
import com.example.material.viewmodel.UserViewModel

@Composable
fun StudentSelectionScreen(
    onBack: () -> Unit,
    onUpdate: () -> Unit,
    viewModel: UserViewModel = hiltViewModel(),
    formViewModel: ClassFormViewModel = hiltViewModel()
) {
    val students by viewModel.users.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    var selected by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        viewModel.loadUsers("STUDENT")
    }

    SwipeRefreshLayout(
        isRefreshing = isLoading,
        onRefresh = { viewModel.loadUsers("STUDENT") }
    ) {
        UserSelectionScreen(
            title = "Select Students",
            users = students,
            selectedUsers = selected,
            onToggleSelect = {
                selected = selected.toMutableSet().apply {
                    if (contains(it)) remove(it) else add(it)
                }
            },
            onBack = onBack,
            onUpdate = {
                // ✅ Save only when pressing update
                formViewModel.setSelectedStudents(selected.toList())
                onUpdate()
            }
        )
    }
}



