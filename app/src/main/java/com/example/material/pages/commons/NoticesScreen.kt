package com.example.material.pages.commons

// keep this single import block ⬇︎
import androidx.annotation.Keep
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.R
import com.example.material.viewmodel.common.NoticeUiState
import com.example.material.viewmodel.common.NoticeViewModel
import com.google.accompanist.swiperefresh.*
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

@Keep
enum class Importance(val raw: String) {      // Gson
    @SerializedName("NORMAL") NORMAL("NORMAL"),
    @SerializedName("MEDIUM") MEDIUM("MEDIUM"),
    @SerializedName("HIGH")   HIGH("HIGH");
}


data class Notice(
    val date: LocalDate,
    val topic: String,
    val body: String,
    val importance: Importance
)

/* …your imports stay unchanged… */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticesScreen(
    notices: List<Notice>,
    onBack: () -> Unit = {}
) {
    val grouped = notices.sortedByDescending { it.date }.groupBy { it.date }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notices") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { pad ->

        /* ───────────── 𝟙. EMPTY ───────────── */
        if (grouped.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pad),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter  = painterResource(R.drawable.report),   // ⬅️ your drawable
                    contentDescription = null,
                    tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(220.dp)
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    "No notices yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Scaffold
        }

        /* ───────────── 𝟚. LIST ───────────── */
        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            grouped.forEach { (date, list) ->

                /* date heading */
                item {
                    Text(
                        date.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(list) { notice -> NoticeCard(notice) }
            }
        }
    }
}



@Composable
private fun NoticeCard(notice: Notice) {


    val container = when (notice.importance) {
        Importance.NORMAL -> Color(0xFFDCEDC8)   // light green
        Importance.MEDIUM -> Color(0xFFFFF9C4)   // light yellow
        Importance.HIGH   -> Color(0xFFFFCDD2)   // light red
    }


    val onContainer = if (container.luminance() < 0.5f)
        Color.White else Color.Black

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = container,
            contentColor   = onContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 88.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(notice.topic, style = MaterialTheme.typography.titleMedium)
            Text(notice.body,  style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@Composable
fun NoticesRoute(
    onBack: () -> Unit,
    vm: NoticeViewModel = hiltViewModel()
) {
    val uiState by vm.state.collectAsState()

    val isRefreshing = uiState is NoticeUiState.Loading &&
            (uiState as? NoticeUiState.Success)?.list?.isNotEmpty() == true

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = vm::refresh
    ) {
        when (uiState) {
            NoticeUiState.Loading -> Box(
                Modifier.fillMaxSize(),
                Alignment.Center
            ) { CircularProgressIndicator() }

            is NoticeUiState.Error -> Box(
                Modifier.fillMaxSize(),
                Alignment.Center
            ) {
                Text(
                    (uiState as NoticeUiState.Error).msg,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is NoticeUiState.Success -> NoticesScreen(
                notices = (uiState as NoticeUiState.Success).list,
                onBack  = onBack
            )
        }
    }
}
