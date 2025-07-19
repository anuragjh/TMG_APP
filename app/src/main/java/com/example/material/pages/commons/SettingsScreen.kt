package com.example.material.pages.commons

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.material.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToStaticPage: (heading: String, content: String) -> Unit,
    onMyAccClick: () -> Unit = {},
    onUpdateClick: () -> Unit = {},
    onSecurityClick: () -> Unit = {}
) {
    val settingsItems = listOf(
        Triple(Icons.Default.AccountCircle, "My Account", onMyAccClick),
        Triple(Icons.Default.Lock, "Security", onSecurityClick),
        Triple(Icons.Default.Android, "App Updates", onUpdateClick),
        Triple(Icons.Default.Article, "Community Guidelines") {
            onNavigateToStaticPage(
                "Community Guidelines", """
                • Mobile phones SHOULD NOT be used inside the Institute Premises and Classroom. Submit in office or keep in your bag. Take teacher's permission in emergencies.
                • DO NOT LOITER in the premises before or after classes. Wait only in the lobby or designated areas.
                • KEEP THE CLASSROOM CLEAN. Use the dustbin available inside or outside the class.
                • Chewing gum is STRICTLY PROHIBITED in the premises.
                • Wear DECENT CLOTHES to show modesty, self-respect, and promote cultural values.
                • DO NOT maintain personal contact with faculty via WhatsApp or social media.
                • Use only the official WhatsApp group and classroom discussion for doubt clearing.
                • For queries: call 8910712592 or email themoderngurukultmg@gmail.com.
                • For online fee payments: Send name, class, and month details to 8013305355.
                • Pay fees in advance; must be cleared within 1st week of every month.
                • Notify the institute on official WhatsApp for any absences with valid reason.
                • For any queries, feel free to reach out to the management.
            """.trimIndent()
            )
        },
        Triple(Icons.Default.Security, "Privacy Policy") {
            onNavigateToStaticPage(
                "Privacy Policy", """
                • We respect your privacy and ensure your data is protected.
                • Only your name, phone number, and email ID are collected for app-related use.
                • Phone and email are used solely to contact parents or share reports.
                • Your full name is displayed for identification within the app.
                • We do not sell, share, or misuse your personal data.
                • Our systems only process data to improve user experience securely.
            """.trimIndent()
            )
        },
        Triple(Icons.Default.Description, "Terms of Service") {
            onNavigateToStaticPage(
                "Terms of Service", """
                • By using this app, you agree to abide by The Modern Gurukul (TMG) policies.
                • Violating the community guidelines may result in account suspension or permanent ban.
                • DO NOT share your login credentials with anyone except your parents or guardians.
                • Misuse of the app in any form is strictly prohibited.
                • Use of this app within institute premises is not allowed unless permitted by a teacher.
                • The app is a privilege provided for academic purposes only.
                • We reserve the right to revoke access in case of misuse.
            """.trimIndent()
            )
        },
        Triple(Icons.Default.Help, "Support") {
            onNavigateToStaticPage(
                "Support", """
                • For help, call us at 8910712592 or email themoderngurukultmg@gmail.com.
                • You can reach out for technical assistance, academic doubts (via classroom/official groups), or fee-related queries.
                • We’re here to help and improve your learning experience.
            """.trimIndent()
            )
        },
        Triple(Icons.Default.Info, "About Us") {
            onNavigateToStaticPage(
                "About Us", """
                The Modern Gurukul (TMG) is dedicated to providing quality education and holistic development.
                Our app aims to enhance communication between students, parents, and teachers.
                We strive to create a supportive learning environment for all our students.
            """.trimIndent()
            )
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp) // Generous horizontal padding
        ) {
            Spacer(modifier = Modifier.height(24.dp)) // More space after the app bar

            Text(
                text = "General",
                style = MaterialTheme.typography.labelLarge, // Appropriate style for section heading
                color = MaterialTheme.colorScheme.primary, // Highlight section heading
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .align(Alignment.Start)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp), // Slightly reduced spacing between cards for denser list
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Make LazyColumn take available space
                contentPadding = PaddingValues(bottom = 24.dp) // Padding at the bottom of the scrollable list
            ) {
                // Displaying "My Account", "Security", "App Updates" first as primary settings
                items(settingsItems.subList(0, 3)) { (icon, label, onClick) ->
                    SettingsCard(icon = icon, label = label, onClick = onClick)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp)) // Space before the policy section
                    Text(
                        text = "Policies & Support",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .align(Alignment.Start)
                    )
                }

                items(settingsItems.subList(3, settingsItems.size)) { (icon, label, onClick) ->
                    SettingsCard(icon = icon, label = label, onClick = onClick)
                }
                item{
                    Spacer(modifier = Modifier.height(24.dp)) // Space above the footer
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "App Version ${BuildConfig.VERSION_NAME}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp)) // Padding at the very bottom
                }
                }
            }

            // Footer for App Version

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp), // Fixed height for consistent look
        shape = RoundedCornerShape(12.dp), // Slightly less rounded than main cards for a distinct feel
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh, // A more prominent surface color for interactive elements
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Subtle elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp), // Generous padding
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(28.dp), // Slightly larger icons
                    tint = MaterialTheme.colorScheme.primary // Primary color for icons
                )
                Spacer(modifier = Modifier.width(18.dp)) // Increased space between icon and text
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium, // Standard for list items
                    fontWeight = FontWeight.Normal // Standard weight
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForwardIos, // Modern iOS-style arrow
                contentDescription = "Navigate",
                modifier = Modifier.size(20.dp), // Consistent arrow size
                tint = MaterialTheme.colorScheme.onSurfaceVariant // Subtler tint for arrow
            )
        }
    }
}