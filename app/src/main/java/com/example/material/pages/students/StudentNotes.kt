
package com.example.material.pages.students

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.material.R
import com.example.material.pages.teacher.NoteCardState
import com.example.material.pages.teacher.NoteItem
import com.example.material.utils.DownloadAndOpenNote
import com.example.material.viewmodel.common.NotesUiState
import com.example.material.viewmodel.common.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesStudentScreen(
 viewModel: NotesViewModel = hiltViewModel()
) {
 val uiState by viewModel.state.collectAsState()

 var downloadingUrl by remember { mutableStateOf<String?>(null) }
 var downloadPct by remember { mutableStateOf(0) }
 var selectedNote by remember { mutableStateOf<NoteItem?>(null) }

 selectedNote?.let { note ->
  note.publicUrl?.let { url ->
   DownloadAndOpenNote(
    remoteUrl = url,
    displayName = note.name.ifBlank { "note" },
    onProgress = { downloadPct = it },
    onFinished = {
     selectedNote = null
     downloadingUrl = null
     downloadPct = 0
    }
   )
   downloadingUrl = url
  } ?: run {
   selectedNote = null
  }
 }

 Scaffold(
  topBar = {
   CenterAlignedTopAppBar(
    title = {
     Text(
      text = "Study Material",
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
 ) { pad ->
  Column(
   modifier = Modifier
    .fillMaxSize()
    .padding(pad)
  ) {
   when (uiState) {
    NotesUiState.Loading -> Box(
     modifier = Modifier.fillMaxSize(),
     contentAlignment = Alignment.Center
    ) {
     CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }

    is NotesUiState.Error -> Box(
     modifier = Modifier.fillMaxSize(),
     contentAlignment = Alignment.Center
    ) {
     Text(
      (uiState as NotesUiState.Error).msg,
      color = MaterialTheme.colorScheme.error,
      style = MaterialTheme.typography.bodyLarge
     )
    }

    is NotesUiState.Success -> {
     val grouped = (uiState as NotesUiState.Success).grouped

     if (grouped.isEmpty()) {
      EmptyNotesContent()
     } else {
      LazyColumn(
       modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
       verticalArrangement = Arrangement.spacedBy(16.dp),
       contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
      ) {
       item {
        Text(
         text = "Tap any to view or download.",
         style = MaterialTheme.typography.bodyLarge,
         color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
       }

       grouped.forEach { (cls, notes) ->
        item {
         Text(
          text = "Class: $cls",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.primary
         )
         Spacer(Modifier.height(8.dp))
        }

        items(notes.filter { !it.publicUrl.isNullOrBlank() }) { note ->
         val pct = note.publicUrl.takeIf { it == downloadingUrl }?.let { downloadPct }
         NoteCard(
          note = note,
          progressPct = pct,
          isSelected = (selectedNote == note),
          onClick = { selectedNote = note },
          onLongPress = { }
         )
        }
        item { Spacer(Modifier.height(16.dp)) }
       }
      }
     }
    }
   }
  }
 }
}

@Composable
private fun EmptyNotesContent() {
 Column(
  modifier = Modifier
   .fillMaxSize()
   .padding(horizontal = 20.dp),
  horizontalAlignment = Alignment.CenterHorizontally,
  verticalArrangement = Arrangement.Center
 ) {
  Image(
   painter = painterResource(R.drawable.booklover),
   contentDescription = "No study material",
   modifier = Modifier
    .fillMaxWidth(0.7f)
    .aspectRatio(1f)
    .clip(RoundedCornerShape(24.dp))
    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    .padding(24.dp)
  )
  Spacer(Modifier.height(32.dp))
  Text(
   text = "No notes yet 📚",
   style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
   color = MaterialTheme.colorScheme.onSurface
  )
  Spacer(Modifier.height(8.dp))
  Text(
   text = "Hang tight, your teacher will upload soon!",
   style = MaterialTheme.typography.bodyLarge,
   color = MaterialTheme.colorScheme.onSurfaceVariant,
   textAlign = TextAlign.Center,
   modifier = Modifier.padding(horizontal = 24.dp)
  )
 }
}
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


/* ───────────────────────── helper ──────────────────────────────── */
