package com.example.material.pages.teacher

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.api.ClassResponse
import com.example.material.viewmodel.teacher.UploadNotesUiState
import com.example.material.viewmodel.teacher.UploadNotesViewModel
import com.example.material.viewmodel.teacher.UploadStatus


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadNotesScreen(
    onBack: () -> Unit,
    vm: UploadNotesViewModel = hiltViewModel()
) {

    /* ---------- state ---------- */
    val ctx       = LocalContext.current
    val uiState   by vm.uiState
    val upStatus  by vm.uploadStatus

    var dialogClass by remember { mutableStateOf<ClassResponse?>(null) }  // <‑‑ change
    var pickedUri   by remember { mutableStateOf<Uri?>(null) }
    var docName     by remember { mutableStateOf("") }

    val picker = rememberLauncherForActivityResult(GetContent()) { uri -> pickedUri = uri }

    val validExtensions = listOf("pdf", "jpg", "jpeg", "png", "doc", "docx", "ora")
    val fileNameRegex = remember { Regex("^.+\\.(${validExtensions.joinToString("|")})$", RegexOption.IGNORE_CASE) }

    val isNameValid = remember(docName) { fileNameRegex.matches(docName.trim()) }


    LaunchedEffect(Unit) { vm.refresh() }

    LaunchedEffect(upStatus) {
        if (upStatus is UploadStatus.Done) {
            Toast.makeText(ctx, (upStatus as UploadStatus.Done).message, Toast.LENGTH_LONG).show()
            vm.resetStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Notes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    )  { pad ->
                Box(
                    Modifier
                        .padding(pad)
                        .fillMaxSize()
                ) {

                    /* ---------- class list ---------- */
                    when (uiState) {
                        UploadNotesUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                        is UploadNotesUiState.Error -> Text(
                            (uiState as UploadNotesUiState.Error).msg,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )

                        is UploadNotesUiState.Success -> {
                            val classes = (uiState as UploadNotesUiState.Success).classes
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                items(classes) { c ->
                                    ElevatedCard(
                                        onClick = { dialogClass = c },   // store whole object
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        ListItem(
                                            headlineContent = { Text(c.className) },
                                            trailingContent = {
                                                Icon(Icons.Default.ArrowForward, contentDescription = null)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    /* ---------- dialog ---------- */
                    dialogClass?.let { cls ->
                        BasicAlertDialog(
                            onDismissRequest = {
                                dialogClass = null
                                pickedUri   = null
                                docName     = ""
                            }
                        ) {
                            Surface(shape = MaterialTheme.shapes.large, tonalElevation = 6.dp) {
                                Column(Modifier.padding(24.dp)) {

                                    Text("Upload note to ${cls.className}",
                                        style = MaterialTheme.typography.titleLarge)

                                    Spacer(Modifier.height(24.dp))

                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(120.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconButton(onClick = { picker.launch("*/*") }) {
                                            Icon(Icons.Default.Add, null,
                                                modifier = Modifier.size(64.dp),
                                                tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }

                                    pickedUri?.lastPathSegment?.let {
                                        Text(it, style = MaterialTheme.typography.bodySmall)
                                    }

                                    Spacer(Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = docName,
                                        onValueChange = { docName = it },
                                        label = { Text("Document name") },
                                        isError = docName.isNotBlank() && !isNameValid,
                                        supportingText = {
                                            if (docName.isNotBlank() && !isNameValid) {
                                                Text("Must end with a valid extension: ${validExtensions.joinToString(", ")}")
                                            }
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )


                                    Spacer(Modifier.height(24.dp))

                                    Button(
                                        onClick = {
                                            vm.upload(cls, pickedUri!!, docName.trim())
                                            dialogClass = null
                                            pickedUri = null
                                            docName = ""
                                        },
                                        enabled = pickedUri != null && isNameValid,
                                        modifier = Modifier.fillMaxWidth()
                                    ) { Text("Upload / Create") }
                                }
                            }
                        }
                    }

                    /* ---------- uploading spinner ---------- */
                    if (upStatus == UploadStatus.Uploading) {
                        AlertDialog(
                            onDismissRequest = {},
                            confirmButton = {},
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(Modifier.size(24.dp))
                                    Spacer(Modifier.width(12.dp))
                                    Text("Uploading…")
                                }
                            }
                        )
                    }
                }
            }
        }

