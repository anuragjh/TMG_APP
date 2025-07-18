package com.example.material.pages.commons

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.material.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToStaticPage: (heading: String, content: String) -> Unit,
    onMyAccClick: () -> Unit = {},
   onUpdateClick: () -> Unit = {},
    onSecurityClick: () -> Unit = { /* Handle security click */ }
) {
    val settingsItems = listOf(
        Triple(Icons.Default.AccountCircle, "My Account", onMyAccClick),

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

        Triple(Icons.Default.Lock, "Security", onSecurityClick),

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

        Triple(Icons.Default.Android, "App Updates",onUpdateClick),

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


    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(22.dp))


            Text(
                text = "Setting & Info",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 12.dp)
                    .align(Alignment.Start)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(settingsItems) { (icon, label, onClick) ->
                    Card(
                        onClick = onClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Navigate",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

