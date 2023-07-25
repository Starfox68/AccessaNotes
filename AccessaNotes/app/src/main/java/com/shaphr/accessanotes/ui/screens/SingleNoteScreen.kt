package com.shaphr.accessanotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shaphr.accessanotes.data.models.UiNote
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.ui.viewmodels.NoteRepositoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleNoteScreen(noteID: Int, viewModel: NoteRepositoryViewModel = hiltViewModel()) {
    val note = remember { mutableStateOf<UiNote?>(null) }
    var ttsButtonText by remember { mutableStateOf("Read Notes") }

    LaunchedEffect(noteID) {
        viewModel.getNote(noteID).collect { value ->
            note.value = value
        }
    }

    Column(Modifier.fillMaxSize()) {
        val config = LocalConfiguration

        // Header
        Box(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(Color.LightGray)
        ) {
            // Back button
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { navController.popBackStack() }
            )
            //Text
            Text(
                text = note.value?.title ?: "default",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)

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
                .background(Color.White)
        ) {
            TextField(
                value = note.value?.summarizeContent ?: "",
                onValueChange = { newValue ->
                    note.value?.summarizeContent = newValue
                    viewModel.updateNote(note!!)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .height((config.current.screenHeightDp * 0.82).dp)
                    .fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.onTextToSpeech(note.value?.summarizeContent ?: "default")
                    ttsButtonText = if (viewModel.isSpeaking) {
                        "Stop Reading"
                    } else {
                        "Read Notes"
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(text = ttsButtonText)
            }
        }
    }
}

@Preview
@Composable
fun SingleNoteScreenPreview() {
    SingleNoteScreen(1, navController = rememberNavController())
}