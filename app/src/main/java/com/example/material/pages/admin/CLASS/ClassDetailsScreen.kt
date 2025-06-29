package com.example.material.pages.admin.CLASS

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.R
import com.example.material.viewmodel.ClassDetailsState
import com.example.material.viewmodel.ClassDetailsViewModel
import java.text.SimpleDateFormat
import java.util.*

fun formatTimestamp(seconds: Long): String {
    val date = Date(seconds * 1000)
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(date)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailsScreen(
    className: String,
    onBack: () -> Unit = {},
    onComplete: () -> Unit = {},
    viewModel: ClassDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    val state by viewModel.classDetailsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadClassDetails(className)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = className, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (state) {
            is ClassDetailsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ClassDetailsState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (state as ClassDetailsState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is ClassDetailsState.Success -> {
                val details = (state as ClassDetailsState.Success).data

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(160.dp)
                            .padding(vertical = 24.dp)
                    )

                    Text(
                        text = "Class Information",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    listOf(
                        Triple(Icons.Filled.Assessment, "Class Name", details.className),
                        Triple(Icons.Filled.CalendarToday, "Created On", formatTimestamp(details.createdAt.seconds)),
                        Triple(Icons.Filled.AttachMoney, "Fees", "₹${details.fees}")
                    ).forEach { (icon, label, value) ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = MaterialTheme.shapes.large,
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(text = label, style = MaterialTheme.typography.labelLarge)
                                    Text(text = value, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(40.dp))

                    OutlinedButton(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error)
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                        Spacer(Modifier.width(8.dp))
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }

                    // Confirm delete dialog
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDialog = false
                                        isDeleting = true
                                        viewModel.deleteClass(
                                            className = details.className,
                                            onSuccess = {
                                                isDeleting = false
                                                Toast.makeText(
                                                    context,
                                                    "Class Deleted Successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                onComplete()
                                            },
                                            onFailure = {
                                                isDeleting = false
                                                Toast.makeText(
                                                    context,
                                                    "Failed to delete class",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    }
                                ) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("No")
                                }
                            },
                            title = { Text("Delete Class") },
                            text = { Text("Are you sure you want to delete ${details.className}?") }
                        )
                    }

                    // Inline loading during deletion
                    if (isDeleting) {
                        AlertDialog(
                            onDismissRequest = {},
                            confirmButton = {},
                            title = { Text("Deleting...") },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                    Text("Please wait")
                                }
                            }
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

