package com.example.material.pages.students

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

// Data class for a Fee Entry
data class FeeEntry(
    val monthYear: YearMonth,
    val isPaid: Boolean,
    val paymentDate: LocalDate? = null // Nullable for unpaid fees
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeScreen(onBack: () -> Unit = {}) {
    // Dummy Data - ordered by monthYear descending (newest first)
    val feeRecords = listOf(
        FeeEntry(YearMonth.of(2025, 7), false), // Current month, unpaid
        FeeEntry(YearMonth.of(2025, 6), false),
        FeeEntry(YearMonth.of(2025, 5), false),
        FeeEntry(YearMonth.of(2025, 4), true, LocalDate.of(2025, 4, 10)),
        FeeEntry(YearMonth.of(2025, 3), true, LocalDate.of(2025, 3, 20)),
        FeeEntry(YearMonth.of(2025, 2), true, LocalDate.of(2025, 2, 15)),
        FeeEntry(YearMonth.of(2025, 1), true, LocalDate.of(2025, 1, 5)),
        FeeEntry(YearMonth.of(2024, 12), true, LocalDate.of(2024, 12, 1)),
        FeeEntry(YearMonth.of(2024, 11), true, LocalDate.of(2024, 11, 8)),
    ).sortedByDescending { it.monthYear } // Ensure newest is at top

    val unpaidMonthsCount = feeRecords.count { !it.isPaid }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Fee Status",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                .padding(paddingValues) // Apply scaffold padding here
                .padding(horizontal = 20.dp, vertical = 16.dp) // Apply outer padding for content
        ) {
            // "Under Development" Warning Text
            Text(
                text = "⚠️ This page is under development. Data shown is dummy/experimental.",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            )

            // Unpaid Months Summary Text (no background)
            val summaryText = if (unpaidMonthsCount > 0) {
                "$unpaidMonthsCount month${if (unpaidMonthsCount > 1) "s" else ""} due"
            } else {
                "All fees are paid up to date!"
            }

            Text(
                text = summaryText,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), // Made larger and extra bold
                color = if (unpaidMonthsCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp) // Increased padding below this text
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(feeRecords) { feeEntry ->
                    FeeCard(feeEntry = feeEntry)
                }

                // "Payments can take upto 1hr" message
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 16.dp), // Adjusted vertical padding
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Payments can take up to 1 hour to update.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Spacer(
                            modifier = Modifier
                                .width(64.dp)
                                .height(2.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                        )
                    }
                }

                // "Get report on mail" message
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp), // Padding at the very bottom
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Get Fees report on your mail",
                            style = MaterialTheme.typography.bodyMedium, // Slightly larger text
                            color = MaterialTheme.colorScheme.onSurfaceVariant, // Regular onSurfaceVariant color
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeeCard(feeEntry: FeeEntry) {
    val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy") // e.g., "July 2025"
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy") // e.g., "22 Jul 2025"

    val cardBackgroundColor = if (feeEntry.isPaid) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer
    val cardContentColor = if (feeEntry.isPaid) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer
    val accentColor = if (feeEntry.isPaid) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
    val statusText = if (feeEntry.isPaid) "Paid" else "Unpaid"
    val statusIcon = if (feeEntry.isPaid) Icons.Default.CheckCircle else Icons.Default.Warning

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor, // Card background color based on status
            contentColor = cardContentColor // Content color based on status
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            // Month with Year & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = feeEntry.monthYear.format(monthYearFormatter),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = accentColor // Accent color for the month/year
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = statusText,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = accentColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Payment Date (if paid)
            if (feeEntry.isPaid) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Payment Date",
                        tint = cardContentColor.copy(alpha = 0.8f), // Use cardContentColor here
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Paid on: ${feeEntry.paymentDate?.format(dateFormatter) ?: "N/A"}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = cardContentColor // Use cardContentColor here
                    )
                }
            } else {
                Text(
                    text = "Payment due for this month.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = cardContentColor.copy(alpha = 0.8f) // Use cardContentColor here
                )
            }
        }
    }
}
