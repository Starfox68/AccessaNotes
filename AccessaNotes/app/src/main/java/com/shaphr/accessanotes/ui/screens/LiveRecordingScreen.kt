package com.shaphr.accessanotes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.shaphr.accessanotes.R
import com.shaphr.accessanotes.ui.viewmodels.LiveRecordingViewModel

@Composable
fun LiveRecordingScreen(
    navBackStackEntry: NavBackStackEntry,
    viewModel: LiveRecordingViewModel = hiltViewModel()
) {
    val arguments = navBackStackEntry.arguments
    val prompt = arguments?.getString("prompt") ?: ""
    viewModel.updatePrompt(prompt)

    val canStop = viewModel.canStop.collectAsState().value
    val canListen = viewModel.canListen.collectAsState().value
    val transcribedText = viewModel.transcribedText.collectAsState().value
    val summarizedContent = viewModel.noteText.collectAsState().value
    LiveRecordingScreenContent(
        transcribedText = transcribedText,
        summarizedContent = summarizedContent,
        onTextToSpeechClick = viewModel::onTextToSpeech,
        onStopClick = viewModel::stopRecording,
        canStop = canStop,
        canListen = canListen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveRecordingScreenContent(
    transcribedText: List<String>,
    summarizedContent: List<String>,
    onTextToSpeechClick: (String) -> Unit,
    onStopClick: () -> Unit,
    canStop: Boolean,
    canListen: Boolean,
    viewModel: LiveRecordingViewModel = hiltViewModel()
) {
    var ttsButtonText by remember { mutableStateOf("Read Summarized Notes") }
    val config = LocalConfiguration


    Scaffold(
        content = { padding ->
            LazyColumn(modifier = Modifier.padding(padding)) {

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


                item{
                    OutlinedButton(onClick = { }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.camera_icon),
                            contentDescription = "Camera Icon",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer (modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = "Add Image")
                    }
                    OutlinedButton(onClick = { onStopClick() }, enabled = canStop
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.stop_icon),
                            contentDescription = "Stop Icon",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer (modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = "Stop Recording")
                    }
                    OutlinedButton(enabled = canListen, onClick = {
                        onTextToSpeechClick(summarizedContent.joinToString(separator = ""))
                        ttsButtonText =
                            if (viewModel.isSpeaking) {
                                "Stop Reading"
                            } else {
                                "Read Summarized Notes"
                            }
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.read_text_icon),
                            contentDescription = "Voice Icon",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer (modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = ttsButtonText)
                    }
                }
            }
        }
    )
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
        onStopClick = { },
        canStop = true,
        canListen = false
    )
}
