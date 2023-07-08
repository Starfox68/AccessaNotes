package com.shaphr.accessanotes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.shaphr.accessanotes.ui.viewmodels.LiveRecordingViewModel

@Composable
fun LiveRecordingScreen(
    navBackStackEntry: NavBackStackEntry,
    viewModel: LiveRecordingViewModel = hiltViewModel()
) {
    val arguments = navBackStackEntry.arguments
    val prompt = arguments?.getString("prompt") ?: ""
    viewModel.updatePrompt(prompt)

    val transcribedText = viewModel.transcribedText.collectAsState().value
    val summarizedContent = viewModel.noteText.collectAsState().value
    LiveRecordingScreenContent(
        transcribedText = transcribedText,
        summarizedContent = summarizedContent,
        onTextToSpeechClick = viewModel::onTextToSpeech,
        onStopClick = viewModel::stopRecording
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveRecordingScreenContent(
    transcribedText: List<String>,
    summarizedContent: List<String>,
    onTextToSpeechClick: (String) -> Unit,
    onStopClick: () -> Unit,
    viewModel: LiveRecordingViewModel = hiltViewModel()
) {
    var ttsButtonText by remember { mutableStateOf("Read Summarized Notes") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            val config = LocalConfiguration

            item {
                Column (modifier = Modifier.height((config.current.screenHeightDp*0.35).dp)
                ) {
                    Text(
                        text = "Transcribed Text",
                        modifier = Modifier.padding(12.dp)
                    )
                    TextField(
                        value = transcribedText.joinToString(separator = ""),
                        onValueChange = { },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            item {
                Column (modifier = Modifier.height((config.current.screenHeightDp*0.35).dp)
                ) {
                    Text(
                        text = "Summarized Notes",
                        modifier = Modifier.padding(12.dp)
                    )
                    TextField(
                        value = summarizedContent.joinToString(separator = ""),
                        onValueChange = { },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(text = "Live Capture")
        }

        Button(
            onClick = { onStopClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(text = "Stop")
        }

        Button(
            onClick = {
                onTextToSpeechClick(summarizedContent.joinToString(separator = ""))
                ttsButtonText =
                    if (viewModel.isSpeaking) {
                        "Stop Reading"
                    } else {
                        "Read Summarized Notes"
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(text = ttsButtonText)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LiveRecordingScreenPreview() {
    LiveRecordingScreenContent(
        transcribedText = listOf(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            "Suspendisse a quam sodales, pretium libero non, pharetra ligula.",
            "Duis ac semper erat",
            "Duis malesuada facilisis lorem, eget cursus massa fermentum at.",
            "Morbi efficitur aliquam molestie."
        ),
        summarizedContent = listOf(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            "Suspendisse a quam sodales, pretium libero non, pharetra ligula.",
            "Duis ac semper erat",
            "Duis malesuada facilisis lorem, eget cursus massa fermentum at.",
            "Morbi efficitur aliquam molestie."
        ),
        onTextToSpeechClick = { },
        onStopClick = { }
    )
}
