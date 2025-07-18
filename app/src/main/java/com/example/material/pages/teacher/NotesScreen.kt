package com.example.material.pages.teacher

/* ───────────────────────────── imports ───────────────────────────── */
import androidx.annotation.Keep
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.utils.DownloadAndOpenNote
import com.example.material.viewmodel.common.NotesUiState
import com.example.material.viewmodel.common.NotesViewModel
import com.google.gson.annotations.SerializedName

/* ───────────────────────────── model ─────────────────────────────── */
@Keep         // androidx.annotation.Keep
data class NoteItem(
    @SerializedName("name")       val name: String = "",
    @SerializedName("publicUrl")  val publicUrl: String? = null,
    @SerializedName("className")  val className: String = "",
    @SerializedName("author")     val author: List<String> = emptyList()
)
 {
    val type: String
        get() = publicUrl
            ?.substringAfterLast('.', "")
            ?.lowercase() ?: ""
}


/* ───────────────────────────── NoteCard state for AnimatedContent ── */
sealed class NoteCardState {
    data class Progress(val percent: Int) : NoteCardState()
    object Selected : NoteCardState()
    object Default  : NoteCardState()
}

/* ───────────────────────────── Screen ────────────────────────────── */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun NotesScreen(
    onAddNoteClick: () -> Unit = {},
    viewModel      : NotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()

    /* ---- runtime UI state ---- */
    var downloadingUrl by remember { mutableStateOf<String?>(null) }
    var downloadPct    by remember { mutableStateOf(0) }
    var selectedNote   by remember { mutableStateOf<NoteItem?>(null) }

    val selectedNotes  = remember { mutableStateListOf<NoteItem>() }
    val isSelection    by derivedStateOf { selectedNotes.isNotEmpty() }
    val isDeleting by viewModel.isDeleting.collectAsState()

    /* ---- file download side‑effect ---- */
    /* ---- file‑download side effect ---- */
    selectedNote?.let { note ->
        val url = note.publicUrl         // url is String?
        if (url != null) {               // ← only launch if we actually have an URL
            DownloadAndOpenNote(
                remoteUrl   = url,       // now non‑null
                displayName = note.name.ifBlank { "note" },
                onProgress  = { downloadPct = it },
                onFinished  = {
                    selectedNote   = null
                    downloadingUrl = null
                    downloadPct    = 0
                }
            )
            downloadingUrl = url         // keep for progress indicator
        } else {
            selectedNote = null          // nothing to open
        }
    }


    /* ---- UI ---- */
    Scaffold(
        topBar = {
            /* slide‑in TopBar only in selection mode */
            AnimatedVisibility(
                visible = isSelection,
                enter   = slideInVertically { -it } + fadeIn(),
                exit    = slideOutVertically { -it } + fadeOut()
            ) {
                CenterAlignedTopAppBar(
                    title = { Text("${selectedNotes.size} selected") },
                    navigationIcon = {
                        IconButton(onClick = { selectedNotes.clear() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (uiState is NotesUiState.Success) {
                                val all = (uiState as NotesUiState.Success).grouped
                                    .values.flatten()
                                if (selectedNotes.size == all.size) selectedNotes.clear()
                                else {
                                    selectedNotes.clear()
                                    selectedNotes.addAll(all)
                                }
                            }
                        }) {
                            Icon(Icons.Default.RadioButtonChecked, "Select All")
                        }
                        IconButton(
                            onClick = {
                                viewModel.deleteNotes(selectedNotes.map { it.name })
                                selectedNotes.clear()
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                )
            }
            /* global overlay while delete is in flight */
            if (isDeleting) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    ) { pad ->

        when (uiState) {
            NotesUiState.Loading -> Box(
                Modifier.fillMaxSize().padding(pad),
                Alignment.Center
            ) { CircularProgressIndicator() }

            is NotesUiState.Error -> Box(
                Modifier.fillMaxSize().padding(pad),
                Alignment.Center
            ) { Text((uiState as NotesUiState.Error).msg) }

            is NotesUiState.Success -> {
                val grouped = (uiState as NotesUiState.Success).grouped

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(pad).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    /* header */
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text("Notes", style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("Browse and manage class notes",
                            style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(12.dp))
                    }

                    /* add‑note card */
                    item {
                        ElevatedCard(
                            onClick  = onAddNoteClick,
                            modifier = Modifier.fillMaxWidth().height(140.dp)
                        ) {
                            Column(
                                Modifier.fillMaxSize().padding(16.dp),
                                Arrangement.Center,
                                Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Add, null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("Add Notes", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }

                    if (grouped.isEmpty()) {
                        item {
                            Column(
                                Modifier.fillMaxWidth().padding(top = 48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NoteAdd,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "No notes uploaded yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        /* group sections */
                        grouped.forEach { (cls, notes) ->
                            item {
                                Text(cls, style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                            }
                            items(notes.filter { !it.publicUrl.isNullOrBlank() }) { note ->
                                val pct = note.publicUrl.takeIf { it == downloadingUrl }?.let { downloadPct }
                                NoteCard(
                                    note        = note,
                                    progressPct = pct,
                                    isSelected  = note in selectedNotes,
                                    onClick     = { selectedNote = note },
                                    onLongPress = {
                                        if (note in selectedNotes) selectedNotes.remove(note)
                                        else selectedNotes.add(note)
                                    }
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}

/* ───────────────────────── NoteCard ─────────────────────────────── */
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
private fun NoteCard(
    note        : NoteItem,
    progressPct : Int? = null,
    isSelected  : Boolean,
    onClick     : () -> Unit,
    onLongPress : () -> Unit
) {
    /* motion parameters */
    val scale by animateFloatAsState(targetValue = if (isSelected) 0.96f else 1f, tween(200))
    val elev  by animateDpAsState(targetValue = if (isSelected) 8.dp else 2.dp, tween(200))
    val borderColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline
    )




    /* derive composite state for icon area */
    val cardState: NoteCardState = when {
        progressPct != null -> NoteCardState.Progress(progressPct)
        isSelected          -> NoteCardState.Selected
        else                -> NoteCardState.Default
    }

    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = elev),
        modifier  = Modifier
            .scale(scale)
            .fillMaxWidth()
            .height(90.dp)
            .combinedClickable(
                onClick     = { if (isSelected) onLongPress() else onClick() },
                onLongClick = { onLongPress() }
            )
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            /* left icon + name */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (note.type) {
                        "pdf"               -> Icons.Default.PictureAsPdf
                        "doc", "docx"       -> Icons.Default.Description
                        "jpg", "jpeg", "png"-> Icons.Default.Image
                        else                -> Icons.Default.InsertDriveFile
                    },
                    contentDescription = null,
                    tint    = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(note.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        note.type.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            /* right AnimatedContent */
            AnimatedContent(
                targetState = cardState,
                transitionSpec = { fadeIn(tween(120)) with fadeOut(tween(120)) },
                label = "IconChange"
            ) { state ->
                when (state) {
                    is NoteCardState.Progress -> {
                        Box(Modifier.size(28.dp), Alignment.Center) {
                            CircularProgressIndicator(
                                progress    = state.percent / 100f,
                                strokeWidth = 3.dp,
                                modifier    = Modifier.fillMaxSize()
                            )
                        }
                    }
                    NoteCardState.Selected -> {
                        Icon(Icons.Default.CheckCircle, null,
                            tint = MaterialTheme.colorScheme.primary)
                    }
                    NoteCardState.Default -> {
                        Icon(Icons.Default.ArrowForward, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
