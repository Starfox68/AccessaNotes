package com.shaphr.accessanotes.ui.screens

import android.Manifest
import android.net.Uri
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceIn
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
import java.net.URLDecoder

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

    val existingAudioStr = arguments?.getString("existingAudio")
   if (existingAudioStr != "0") {
       val existingAudio = existingAudioStr?.let { Uri.parse(URLDecoder.decode(it, "UTF-8")) }
       viewModel.setAudioExists(existingAudio)
   }

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
    // Update text depending on if currently speaking
    var ttsButtonText by remember { mutableStateOf("Read Summarized Notes") }
    var isSpeaking = false
    val screenHeight = (LocalConfiguration.current.screenHeightDp).dp
    // Can be altered dynamically through UI input
    var transcriptHeight by remember { mutableStateOf(screenHeight * 0.35F) }
    var summaryHeight by remember { mutableStateOf(screenHeight * 0.35F) }

    Scaffold(
        content = { padding ->
            LazyColumn(modifier = Modifier.padding(padding)) {

                item {
                    Column(
                        modifier = Modifier.height(transcriptHeight).padding(4.dp)
                    ) {
                        Text(
                            text = "Transcribed Text",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextField(
                            value = transcribedText.joinToString(separator = ""),
                            onValueChange = { },
                            modifier = Modifier.fillMaxSize(),
                            textStyle = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                item {
                    Divider(color = MaterialTheme.colorScheme.tertiary, thickness = 4.dp,
                        modifier = Modifier.padding(4.dp).pointerInput(Unit) {
                            // Update heights based on user drag input
                            detectVerticalDragGestures { _, dragAmount ->
                                transcriptHeight = (transcriptHeight + dragAmount.dp).coerceIn(
                                    screenHeight * 0.15F,
                                    screenHeight * 0.55F
                                )
                                summaryHeight = (summaryHeight - dragAmount.dp).coerceIn(
                                    screenHeight * 0.15F,
                                    screenHeight * 0.55F
                                )
                            }
                        })
                }

                item {
                    Column(
                        modifier = Modifier.height(summaryHeight).padding(4.dp)
                    ) {
                        Text(
                            text = "Summarized Notes",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextField(
                            value = summarizedContent.joinToString(separator = ""),
                            onValueChange = { },
                            modifier = Modifier.fillMaxSize(),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }
                }


                item {
                    OutlinedButton(
                        modifier = Modifier.padding(start = 4.dp),
                        onClick = {
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
                        Text(text = "Add Image", style = MaterialTheme.typography.bodyMedium)
                    }
                    OutlinedButton(
                        onClick = { onStopClick() },
                        enabled = canStop,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.stop_icon),
                            contentDescription = "Stop Icon",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = "Stop Recording", style = MaterialTheme.typography.bodyMedium)
                    }
                    OutlinedButton(
                        enabled = canListen,
                        modifier = Modifier.width(235.dp).padding(start = 4.dp),
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
                        Spacer(
                            modifier = Modifier
                                .size(ButtonDefaults.IconSpacing)
                                .weight(1F)
                        )
                        Text(text = ttsButtonText, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = {
                            stopTextToSpeech()
                            navController.navigate(Destination.SessionStartAndEndScreen.route)
                        }) {
                            Text(text = "Discard", style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))

                        OutlinedButton(enabled = !canStop, onClick = {
                            stopTextToSpeech()
                            onSaveClick()
                        }) {
                            Text(text = "Save", style = MaterialTheme.typography.bodyMedium)
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
