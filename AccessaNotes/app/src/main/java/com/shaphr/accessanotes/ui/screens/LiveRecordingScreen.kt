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
    val content = viewModel.noteText.collectAsState().value
    LiveRecordingScreenContent(
        content = content,
        onStartRecordingClick = viewModel::onStartRecording,
        onStopRecordingClick = viewModel::onStopRecording
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveRecordingScreenContent(
    content: List<String>,
    onStartRecordingClick: () -> Unit,
    onStopRecordingClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Record Session",
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn {
            val config = LocalConfiguration
            item {
                Column (modifier = Modifier.height((config.current.screenHeightDp*0.35).dp)
                ) {
                    TextField(
                        value = content.joinToString(separator = ""),
                        onValueChange = { },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            item {
                Column (modifier = Modifier.height((config.current.screenHeightDp*0.35).dp)
                ) {
                    TextField(
                        value = content.joinToString(separator = ""),
                        onValueChange = { },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        Button(
            onClick = onStartRecordingClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Live Capture")
        }

        Button(
            onClick = onStopRecordingClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Stop")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LiveRecordingScreenPreview() {
    LiveRecordingScreenContent(
        content = listOf(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            "Suspendisse a quam sodales, pretium libero non, pharetra ligula.",
            "Duis ac semper erat",
            "Duis malesuada facilisis lorem, eget cursus massa fermentum at.",
            "Morbi efficitur aliquam molestie."
        ),
        onStartRecordingClick = {},
        onStopRecordingClick = {},
    )
}
