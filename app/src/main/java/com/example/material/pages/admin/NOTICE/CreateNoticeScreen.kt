// pages/admin/notice/CreateNoticeRoute.kt
package com.example.material.pages.admin.notice

/* ------------- imports ------------- */
import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.viewmodel.CreateNoticeState
import com.example.material.viewmodel.CreateNoticeViewModel

/* ------------- public entry – call this from NavGraph ------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoticeRoute(
    onBack: () -> Unit,
    vm: CreateNoticeViewModel = hiltViewModel()
) {
    /* ------ ViewModel →  UI state ------ */
    val uiState by vm.state.collectAsState()
    val sending = uiState is CreateNoticeState.Sending
    val context = LocalContext.current

    /* ------ form state in the screen ------ */
    var topic      by remember { mutableStateOf("") }
    var body       by remember { mutableStateOf("") }
    var role       by remember { mutableStateOf("STUDENT") }
    var importance by remember { mutableStateOf("NORMAL") }

    /* ------ toast / navigation reactions ------ */
    LaunchedEffect(uiState) {
        when (uiState) {
            is CreateNoticeState.Success -> {
                Toast.makeText(context, "Notice published!", Toast.LENGTH_SHORT).show()
                vm.reset()
                onBack()
            }
            is CreateNoticeState.Error -> {
                Toast.makeText(
                    context,
                    (uiState as CreateNoticeState.Error).msg,
                    Toast.LENGTH_LONG
                ).show()
                vm.reset()
            }
            else -> {}
        }
    }

    /* ------------------ UI ------------------ */
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Notice") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            /* topic */
            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Topic") },
                leadingIcon = { Icon(Icons.Default.Title, null) },
                singleLine = true,
                enabled = !sending,
                modifier = Modifier.fillMaxWidth()
            )

            /* body */
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                label = { Text("Body") },
                leadingIcon = { Icon(Icons.Default.Description, null) },
                minLines = 4,
                maxLines = 5,
                enabled = !sending,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
            )

            /* role dropdown */
            DropdownField(
                value = role,
                label = "Audience",
                icon = Icons.Default.Groups,
                options = listOf("STUDENT", "TEACHER"),
                enabled = !sending,
                onSelected = { role = it }
            )

            /* importance dropdown */
            DropdownField(
                value = importance,
                label = "Importance",
                icon = Icons.Default.PriorityHigh,
                options = listOf("NORMAL", "MEDIUM", "HIGH"),
                enabled = !sending,
                onSelected = { importance = it }
            )

            Spacer(Modifier.height(12.dp))

            /* send button / loader */
            FilledTonalButton(
                onClick = { vm.send(topic, body, role, importance) },
                enabled = !sending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (sending) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Default.Send, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Send Notice")
                }
            }
        }
    }
}

/* ---------- reusable dropdown field ---------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    value: String,
    label: String,
    icon: ImageVector,
    options: List<String>,
    enabled: Boolean,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(icon, null) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            enabled = enabled,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}
