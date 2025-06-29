package com.example.material.pages.admin.CLASS

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.pages.admin.USERS.CustomizableSearchBar
import com.example.material.viewmodel.AddUsersViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUsersScreen(
    className: String,
    viewModel: AddUsersViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    val context = LocalContext.current
    var navigateToHome by remember { mutableStateOf(false) }

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }

    val isLoading by viewModel.loading
    val isUpdating by viewModel.updating
    val teachers = viewModel.nonTeachers
    val students = viewModel.nonStudents

    val teachersChecked = remember { mutableStateMapOf<String, Boolean>() }
    val studentsChecked = remember { mutableStateMapOf<String, Boolean>() }

    val atLeastOneSelected = remember(teachersChecked, studentsChecked) {
        derivedStateOf {
            teachersChecked.values.any { it } || studentsChecked.values.any { it }
        }
    }

    val users = if (selectedTab == 0) teachers else students
    val checkedMap = if (selectedTab == 0) teachersChecked else studentsChecked

    val filteredUsers = users.filter { it.name.contains(query, ignoreCase = true) }

    if (navigateToHome) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Users added to class", Toast.LENGTH_SHORT).show()
            onComplete()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadUsers(className)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Users") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading && teachers.isEmpty() && students.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Loading users...")
                }
            }
        } else {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = { viewModel.loadUsers(className) },
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = {
                                selectedTab = 0
                                query = ""
                            },
                            text = { Text("TEACHER") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                                query = ""
                            },
                            text = { Text("STUDENT") }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    CustomizableSearchBar(
                        query = query,
                        onQueryChange = { query = it },
                        onSearch = {},
                        searchResults = filteredUsers.map { it.name },
                        onResultClick = { name ->
                            val result = filteredUsers.find { it.name == name }
                            result?.let { checkedMap[it.username] = !(checkedMap[it.username] ?: false) }
                        },
                        placeholder = { Text("Search by name") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (filteredUsers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No results")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(filteredUsers) { user ->
                                val isChecked = checkedMap[user.username] ?: false
                                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                    ListItem(
                                        headlineContent = { Text(user.name) },
                                        supportingContent = { Text(user.username) },
                                        trailingContent = {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { checked ->
                                                    checkedMap[user.username] = checked
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val selectedUsers = buildList {
                                teachersChecked.filterValues { it }.keys.forEach {
                                    add(it to "TEACHER")
                                }
                                studentsChecked.filterValues { it }.keys.forEach {
                                    add(it to "STUDENT")
                                }
                            }

                            viewModel.addSelectedUsers(
                                className = className,
                                selected = selectedUsers,
                                onSuccess = {
                                    navigateToHome = true
                                },
                                onError = { e ->
                                    println("Add failed: ${e.localizedMessage}")
                                    Toast.makeText(context, "Failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        modifier = Modifier.align(Alignment.End),
                        enabled = atLeastOneSelected.value && !isUpdating
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 8.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        Text("Update")
                    }
                }
            }
        }
    }
}
