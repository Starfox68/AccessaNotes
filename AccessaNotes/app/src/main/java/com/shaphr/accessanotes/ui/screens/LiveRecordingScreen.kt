package com.shaphr.accessanotes.ui.screens

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.shaphr.accessanotes.Destination
import com.shaphr.accessanotes.R
import com.shaphr.accessanotes.ui.viewmodels.LiveRecordingViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LiveRecordingScreen(
    navBackStackEntry: NavBackStackEntry,
    navController: NavHostController,
    viewModel: LiveRecordingViewModel = hiltViewModel()
) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    val arguments = navBackStackEntry.arguments
    val prompt = arguments?.getString("prompt") ?: ""
    viewModel.updatePrompt(prompt)

    val canStop = viewModel.canStop.collectAsState().value
    val canListen = viewModel.canListen.collectAsState().value
    val transcribedText = viewModel.transcribedText.collectAsState().value
    val summarizedContent = viewModel.noteText.collectAsState().value
    LiveRecordingScreenContent(
        navController = navController,
        transcribedText = transcribedText,
        summarizedContent = summarizedContent,
        startTextToSpeech = viewModel::startTextToSpeech,
        stopTextToSpeech = viewModel::stopTextToSpeech,
        onStopClick = viewModel::stopRecording,
        onSaveClick = { viewModel.onSave(navController) },
        canStop = canStop,
        canListen = canListen,
        hasCameraPermission = cameraPermissionState.status.isGranted,
        onCameraPermissionRequest = cameraPermissionState::launchPermissionRequest,
        onCameraClick = { navController.navigate(Destination.CameraScreen.route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveRecordingScreenContent(
    navController: NavHostController,
    transcribedText: List<String>,
    summarizedContent: List<String>,
    startTextToSpeech: (String) -> Unit,
    stopTextToSpeech: () -> Unit,
    onStopClick: () -> Unit,
    onSaveClick: () -> Unit,
    canStop: Boolean,
    canListen: Boolean,
    hasCameraPermission: Boolean,
    onCameraPermissionRequest: () -> Unit,
    onCameraClick: () -> Unit,
) {
    var ttsButtonText by remember { mutableStateOf("Read Summarized Notes") }
    var isSpeaking = false
    val config = LocalConfiguration


    Scaffold(
        content = { padding ->
            LazyColumn(modifier = Modifier.padding(padding)) {

                item {
                    Column(
                        modifier = Modifier.height((config.current.screenHeightDp * 0.35).dp)
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
                    Column(
                        modifier = Modifier.height((config.current.screenHeightDp * 0.35).dp)
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


                item {
                    OutlinedButton(onClick = {
                        if (hasCameraPermission) {
                            onCameraClick()
                        } else {
                            onCameraPermissionRequest()
                        }
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.camera_icon),
                            contentDescription = "Camera Icon",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = "Add Image")
                    }
                    OutlinedButton(
                        onClick = { onStopClick() }, enabled = canStop
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.stop_icon),
                            contentDescription = "Stop Icon",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = "Stop Recording")
                    }
                    OutlinedButton(
                        enabled = canListen,
                        modifier = Modifier.width(230.dp),
                        onClick = {
                            ttsButtonText = if (!isSpeaking) {
                                startTextToSpeech(summarizedContent.joinToString(separator = ""))
                                "Stop Reading Notes    "
                            } else {
                                stopTextToSpeech()
                                "Read Summarized Notes"
                            }
                            isSpeaking = !isSpeaking
                        }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.read_text_icon),
                            contentDescription = "Voice Icon",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing).weight(1F))
                        Text(text = ttsButtonText, textAlign = TextAlign.Left)
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = {
                            navController.navigate(Destination.SessionStartAndEndScreen.route)
                        }) {
                            Text(text = "Discard")
                        }

                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))

                        OutlinedButton(enabled = !canStop, onClick = onSaveClick) {
                            Text(text = "Save")
                        }
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
        navController = NavHostController(LocalContext.current),
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
        startTextToSpeech = { },
        stopTextToSpeech = { },
        onStopClick = { },
        onSaveClick = { },
        canStop = true,
        canListen = false,
        hasCameraPermission = true,
        onCameraPermissionRequest = {},
        onCameraClick = {}
    )
}
