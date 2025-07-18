package com.example.material.pages.commons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.viewmodel.common.UpdateUiState
import com.example.material.viewmodel.common.UpdateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TMGUpdateScreen(
    version: String,
    viewModel: UpdateViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val progress by viewModel.downloadProgress.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkUpdate(version)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Update") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        when (state) {
            is UpdateUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is UpdateUiState.Success -> {
                val response = (state as UpdateUiState.Success).response
                val updateAvailable = response.updateRequired && response.data != null

                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = if (updateAvailable) "Update Available" else "No Update Available",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    response.data?.let { data ->
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Size  •  ${data.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "VERSION ${data.version}  |  ${data.releaseMonth}  |  ANDROID 9+",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = data.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (progress in 1..99) {
                            LinearProgressIndicator(
                                progress = progress / 100f,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text("$progress%", modifier = Modifier.align(Alignment.CenterHorizontally))
                        }

                        if (updateAvailable) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                Button(
                                    onClick = {
                                        viewModel.downloadAndInstallApk(data.apkUrl)
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = progress == 0 || progress == 100
                                ) {
                                    Text("Download")
                                }
                            }
                        }

                    }
                }
            }

            is UpdateUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${(state as UpdateUiState.Error).message}")
                }
            }
        }
    }
}




