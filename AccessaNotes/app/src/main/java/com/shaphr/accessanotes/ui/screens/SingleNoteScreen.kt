package com.shaphr.accessanotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shaphr.accessanotes.ui.viewmodels.NoteRepositoryViewModel

@Composable
fun SingleNoteScreen(noteID: Int, viewModel: NoteRepositoryViewModel = hiltViewModel()) {
    val note = viewModel.getNote(noteID)
        Column(Modifier.fillMaxSize()) {
            // Header
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .background(Color.LightGray)
            ) {
                // Back button
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
                //Text
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(16.dp)

                )
                // Share icon
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.Black,
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .padding(16.dp)
                )
            }

            // Body
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .background(Color.White)
            ) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
}

@Preview
@Composable
fun SingleNoteScreenPreview() {
    SingleNoteScreen(1)
}