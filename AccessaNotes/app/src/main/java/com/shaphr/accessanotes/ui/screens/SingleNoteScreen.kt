package com.shaphr.accessanotes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shaphr.accessanotes.ui.viewmodels.NoteRepositoryViewModel

@Composable
fun SingleNoteScreen(noteID: Int, viewModel: NoteRepositoryViewModel = hiltViewModel()) {
    val note = viewModel.getNote(noteID)

    Column(modifier = Modifier.padding(8.dp)) {
        Text(note.title)
        Text("Date: ${note.date}")
        Text(note.content)
    }
}