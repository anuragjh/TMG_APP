package com.example.material.pages.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.material.ui.theme.TMGTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassesCreationScreen(
    onBack: () -> Unit = {}
) {
    var className by remember { mutableStateOf("") }
    var classFee by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Class Creation") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = className,
                onValueChange = { className = it },
                label = { Text("Class Name") },
                leadingIcon = {
                    Icon(Icons.Default.School, contentDescription = "Class Name")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = classFee,
                onValueChange = { classFee = it },
                label = { Text("Class Fee") },
                leadingIcon = {
                    Icon(Icons.Default.AttachMoney, contentDescription = "Class Fee")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { /* TODO: Select Students */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Students")
                }

                OutlinedButton(
                    onClick = { /* TODO: Select Teachers */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Teachers")
                }
            }

            Spacer(Modifier.height(12.dp))

            FilledTonalButton(
                onClick = { /* TODO: Create Class */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Create Class")
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ClassesCreationScreenPreview() {
    TMGTheme {
        ClassesCreationScreen()
    }
}
