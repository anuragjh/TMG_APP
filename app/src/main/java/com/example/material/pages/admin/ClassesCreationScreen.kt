package com.example.material.pages.admin

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.viewmodel.ClassFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassesCreationScreen(
    onBack: () -> Unit = {},
    onStudentTap: () -> Unit = {},
    onTeacherTap: () -> Unit = {},
    formViewModel: ClassFormViewModel = hiltViewModel(),
    onClassCreated: () -> Unit = {} // 🎯 Callback to navigate
) {
    var className by remember { mutableStateOf("") }
    var classFee by remember { mutableStateOf("") }

    val students by formViewModel.selectedStudents.collectAsState()
    val teachers by formViewModel.selectedTeachers.collectAsState()
    val context = LocalContext.current

    // 🎯 Show toast and navigate to AdminHome
    LaunchedEffect(formViewModel.successMessage) {
        if (formViewModel.successMessage.isNotEmpty()) {
            Toast.makeText(context, formViewModel.successMessage, Toast.LENGTH_LONG).show()
            onClassCreated()
        }
    }

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
        Box(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            if (formViewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
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
                            onClick = onStudentTap,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("${students.size} Students")
                        }

                        OutlinedButton(
                            onClick = onTeacherTap,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("${teachers.size} Teachers")
                        }
                    }

                    if (formViewModel.errorMessage.isNotEmpty()) {
                        Text(
                            text = formViewModel.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    FilledTonalButton(
                        onClick = {
                            formViewModel.createClass(className, classFee)
                        },
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
    }
}

