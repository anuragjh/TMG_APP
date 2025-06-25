package com.example.material.pages.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.material.R
import com.example.material.Screen
import com.example.material.viewmodel.OTPViewModel

@Composable
fun OTPVerifyScreen(
    email: String,
    navController: NavController,
    viewModel: OTPViewModel = hiltViewModel()
) {
    val otpLength = 6
    val focusManager = LocalFocusManager.current
    val otpValues = remember { List(otpLength) { mutableStateOf("") } }
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }

    // Observe ViewModel state
    val otpState by viewModel.otpState.collectAsState()
    var showError by remember { mutableStateOf(false) }

    // Handle success or failure
    LaunchedEffect(otpState) {
        when (otpState) {
            is OTPViewModel.OTPState.Success -> {
                val key = (otpState as OTPViewModel.OTPState.Success).key
//                navController.navigate("change_password?key=$key")
                navController.navigate(Screen.ChangePassword.createRoute(key))
                viewModel.resetState()
            }

            is OTPViewModel.OTPState.Error -> {
                showError = true
                viewModel.resetState()
            }

            else -> Unit
        }
    }

    // Autofocus on first box when screen loads
    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "OTP sent to your mail",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Image(
                painter = painterResource(id = R.drawable.otpconfirm),
                contentDescription = "OTP Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(140.dp)
                    .padding(bottom = 32.dp)
            )

            Text(
                text = "Please enter the OTP received",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            // OTP Boxes
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                otpValues.forEachIndexed { index, state ->
                    OutlinedTextField(
                        value = state.value,
                        onValueChange = { newText ->
                            if (newText.length <= 1 && newText.all { it.isDigit() }) {
                                state.value = newText
                                if (newText.isNotEmpty()) {
                                    if (index < otpLength - 1) {
                                        focusRequesters[index + 1].requestFocus()
                                    } else {
                                        focusManager.clearFocus()
                                    }
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = VisualTransformation.None,
                        modifier = Modifier
                            .width(48.dp)
                            .height(56.dp)
                            .focusRequester(focusRequesters[index])
                            .onKeyEvent { keyEvent ->
                                if (keyEvent.type == KeyEventType.KeyDown &&
                                    keyEvent.key == Key.Backspace &&
                                    state.value.isEmpty()
                                ) {
                                    if (index > 0) {
                                        focusRequesters[index - 1].requestFocus()
                                        otpValues[index - 1].value = ""
                                    }
                                    true
                                } else false
                            }
                            .focusable(),
                        textStyle = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center)
                    )
                }
            }

            // Error
            if (showError) {
                Text(
                    text = "Invalid OTP. Please try again.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Loading
            if (otpState is OTPViewModel.OTPState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(vertical = 16.dp),
                    strokeWidth = 3.dp
                )
            }

            // Verify Button
            FilledTonalButton(
                onClick = {
                    val enteredOTP = otpValues.joinToString("") { it.value }
                    showError = enteredOTP.length != otpLength
                    if (!showError) {
                        viewModel.verifyOtp(email, enteredOTP)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "Verify",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

