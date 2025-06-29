package com.example.material.pages.admin.CLASS

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.material.pages.admin.CardListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassUserMnagementScreen(
    className: String,
    onBack: () -> Unit = {},
    onClassAddClick: (String) -> Unit,
    onClassRemoveClick: (String) -> Unit = {},
    onClassDetailsClick: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Class") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    )
    { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CardListItem(
                "Add users to class",
                Icons.Default.Add,
                onClick = { onClassAddClick(className) })
            CardListItem(
                "Remove users from class",
                Icons.Default.Delete,
                onClick = { onClassRemoveClick(className) })
            CardListItem(
                "Class Settings",
                Icons.Default.Settings,
                onClick = {onClassDetailsClick(className) })
        }
    }
}
