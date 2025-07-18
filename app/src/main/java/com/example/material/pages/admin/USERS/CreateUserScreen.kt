package com.example.material.pages.admin.USERS

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.api.CreateUserRequest
import com.example.material.viewmodel.userViewModel.CreateUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(
    onBack: () -> Unit = {},
    onUserCreated: () -> Unit = {},
    viewModel: CreateUserViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf(generatePassword()) }
    var role by remember { mutableStateOf("STUDENT") }
    var username by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf("") }

    val suffix = if (role == "STUDENT") "@tmg.student" else "@tmg.teacher"
    val expanded = remember { mutableStateOf(false) }

    // ViewModel states
    val isLoading by viewModel.loading.collectAsState()
    val serverError by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()

    // Show success toast & trigger callback
    LaunchedEffect(success) {
        if (success) {
            Toast.makeText(context, "✅ User Created!", Toast.LENGTH_SHORT).show()
            onUserCreated()
        }
    }

    fun validate(): Boolean {
        return when {
            name.isBlank() || email.isBlank() || phone.isBlank() || username.isBlank() -> {
                localError = "All fields are required"
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                localError = "Invalid email format"
                false
            }

            !phone.matches(Regex("^[0-9]{10}$")) -> {
                localError = "Phone number must be 10 digits"
                false
            }

            password.length < 8 -> {
                localError = "Password must be at least 8 characters"
                false
            }

            else -> {
                localError = ""
                true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create User") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { if (it.length <= 10) phone = it },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("+91 ") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { if (it.length <= 16) password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded.value,
                onExpandedChange = { expanded.value = !expanded.value }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = role,
                    onValueChange = {},
                    label = { Text("Role") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                    },
                    leadingIcon = { Icon(Icons.Default.PersonOutline, contentDescription = null) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .clickable { expanded.value = true }
                )

                ExposedDropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    listOf("STUDENT", "TEACHER").forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                role = it
                                expanded.value = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                trailingIcon = {
                    Text(
                        text = suffix,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Show error
            if (localError.isNotEmpty()) {
                Text(
                    text = localError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (serverError.isNotEmpty()) {
                Text(
                    text = serverError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(12.dp))

            // Button with loader
            FilledTonalButton(
                onClick = {
                    if (validate()) {
                        viewModel.createUser(
                            CreateUserRequest(
                                name = name,
                                gmail = email,
                                phone = phone,
                                role = role,
                                password = password,
                                username = "$username$suffix"
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create User")
                }
            }
        }
    }
}

fun generatePassword(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..8).map { chars.random() }.joinToString("")
}

