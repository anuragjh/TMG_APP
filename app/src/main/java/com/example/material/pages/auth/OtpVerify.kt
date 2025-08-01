package com.example.material.pages.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.material.R
import com.example.material.viewmodel.OTPViewModel

@Composable
fun OTPVerifyScreen(
    email: String,
    navController: NavController,
    viewModel: OTPViewModel = hiltViewModel()
) {
    val otpLength = 6
    val focusManager = LocalFocusManager.current
    var otpValue by remember { mutableStateOf("") } // Single state for the entire OTP string

    val otpState by viewModel.otpState.collectAsState()
    var showError by remember { mutableStateOf(false) }

    // Handle success or failure
    LaunchedEffect(otpState) {
        when (otpState) {
            is OTPViewModel.OTPState.Success -> {
                val key = (otpState as OTPViewModel.OTPState.Success).key
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

    // Custom VisualTransformation to add spaces between OTP digits
    val otpVisualTransformation = remember(otpLength) {
        OtpVisualTransformation(otpLength)
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

            // Single OTP Input Field
            OutlinedTextField(
                value = otpValue,
                onValueChange = { newValue ->
                    // Filter input to only allow digits and limit length
                    val filteredValue = newValue.filter { it.isDigit() }
                    if (filteredValue.length <= otpLength) {
                        otpValue = filteredValue
                        // Automatically clear focus if OTP is complete
                        if (otpValue.length == otpLength) {
                            focusManager.clearFocus()
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = otpVisualTransformation, // Apply the custom transformation
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight() // Allow height to adjust if needed, or set fixed
                    .padding(bottom = 8.dp),
                textStyle = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
                label = { Text("Enter OTP") } // Optional label
            )

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
                    showError = otpValue.length != otpLength
                    if (!showError) {
                        viewModel.verifyOtp(email, otpValue)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large,
                enabled = otpValue.length == otpLength // Enable button only when OTP is complete
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

// Custom VisualTransformation for OTP
class OtpVisualTransformation(private val otpLength: Int) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmedText = if (text.text.length >= otpLength) {
            text.text.substring(0, otpLength)
        } else {
            text.text
        }

        // Build the visual string with spaces
        val annotatedString = AnnotatedString.Builder().apply {
            for (i in trimmedText.indices) {
                append(trimmedText[i])
                if (i < trimmedText.length - 1) {
                    // Add a space after each digit
                    append(" ")
                }
            }
        }.toAnnotatedString()

        // Map offsets for cursor positioning
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // If original offset is N, transformed offset will be N + (N-1) spaces
                // But only if N > 0, otherwise it's just N
                return if (offset <= 0) 0 else offset + (offset - 1).coerceAtLeast(0)
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Remove spaces when mapping back
                // This is slightly more complex, but a good approximation is:
                // Find how many spaces are before the transformed offset
                val numberOfSpacesBeforeOffset = (offset / 2).coerceAtMost(otpLength - 1)
                return (offset - numberOfSpacesBeforeOffset).coerceAtLeast(0)
            }
        }

        return TransformedText(annotatedString, offsetMapping)
    }
}


