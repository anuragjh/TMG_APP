package com.example.material.pages.admin.CLASS

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier




data class User(
    val name: String,
    val username: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSelectionScreen(
    title: String,
    users: List<User>,
    selectedUsers: Set<String>,
    onToggleSelect: (String) -> Unit,
    onBack: () -> Unit,
    onUpdate: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onUpdate) {
                        Text("Update")
                    }
                }
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
        ) {
            items(users) { user ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { onToggleSelect(user.username) }
                ) {
                    ListItem(
                        headlineContent = {
                            Text(user.name, style = MaterialTheme.typography.titleMedium)
                        },
                        supportingContent = {
                            Text("@${user.username}", style = MaterialTheme.typography.bodySmall)
                        },
                        trailingContent = {
                            Checkbox(
                                checked = selectedUsers.contains(user.username),
                                onCheckedChange = { onToggleSelect(user.username) }
                            )
                        }
                    )
                }
            }
        }
    }
}
