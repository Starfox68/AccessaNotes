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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shaphr.accessanotes.ui.viewmodels.LiveRecordingViewModel

@Composable
fun LiveRecordingScreen(viewModel: LiveRecordingViewModel = hiltViewModel()) {
    val transcribedText = viewModel.transcribedText.collectAsState().value
    val summarizedContent = viewModel.noteText.collectAsState().value
    LiveRecordingScreenContent(
        transcribedText = transcribedText,
        summarizedContent = summarizedContent,
        onTextToSpeechClick = viewModel::onTextToSpeech
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveRecordingScreenContent(
    transcribedText: List<String>,
    summarizedContent: List<String>,
    onTextToSpeechClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            val config = LocalConfiguration

            item {
                Column (modifier = Modifier.height((config.current.screenHeightDp*0.40).dp)
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
                Column (modifier = Modifier.height((config.current.screenHeightDp*0.40).dp)
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
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(text = "Stop")
        }

        Button(
            onClick = onTextToSpeechClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(text = "Read Summarized Notes")
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
        onTextToSpeechClick = {}
    )
}
