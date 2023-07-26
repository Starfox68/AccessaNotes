package com.shaphr.accessanotes.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.shaphr.accessanotes.R
import com.shaphr.accessanotes.data.models.UiNote
import com.shaphr.accessanotes.ui.viewmodels.SingleNoteViewModel

@Composable
fun SingleNoteScreen(
    noteID: Int,
    navController: NavHostController,
    viewModel: SingleNoteViewModel = hiltViewModel()
) {
    val note = viewModel.getNote(noteID).collectAsState(initial = UiNote()).value
    var ttsButtonText by remember { mutableStateOf("Read Summarized Notes") }
    val screenHeight = (LocalConfiguration.current.screenHeightDp).dp
    var transcriptHeight by remember { mutableStateOf(screenHeight * 0.35F) }
    var summaryHeight by remember { mutableStateOf(screenHeight * 0.35F) }

    Column(Modifier.fillMaxSize()) {
        // Header
        Box(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
        ) {
            // Back button
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        viewModel.updateNoteDB(note)
                        navController.popBackStack()
                    }
            )
            //Text
            Text(
                text = note?.title ?: "default",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)

            )
            // Share icon
//            Icon(
//                Icons.Default.Share,
//                contentDescription = "Share",
//                tint = Color.Black,
//                modifier = Modifier
//                    .align(alignment = Alignment.TopEnd)
//                    .padding(16.dp)
//            )
        }

        // Body
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
        ) {
            item {
                Text(
                    text = "Transcribed Text",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 16.sp
                )

                LazyColumn(
                    modifier = Modifier
                        .height(transcriptHeight)
                        .padding(4.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                ) {
                    item {
                        BasicTextField(
                            value = note?.transcript ?: "",
                            onValueChange = { },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 4.dp, end = 4.dp),
                            textStyle = TextStyle.Default.copy(fontSize = 16.sp)
                        )
                    }
                }
            }

            item {
                Divider(color = MaterialTheme.colorScheme.tertiary, thickness = 4.dp,
                    modifier = Modifier
                        .padding(4.dp)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { _, dragAmount ->
                                transcriptHeight = (transcriptHeight + dragAmount.dp).coerceIn(
                                    screenHeight * 0.1F,
                                    screenHeight * 0.6F
                                )
                                summaryHeight = (summaryHeight - dragAmount.dp).coerceIn(
                                    screenHeight * 0.1F,
                                    screenHeight * 0.6F
                                )
                            }
                        }
                )
            }

            item {
                Text(
                    text = "Summarized Notes",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 16.sp
                )

                LazyColumn(
                    modifier = Modifier
                        .height(summaryHeight)
                        .padding(4.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                ) {
                    note?.items?.forEachIndexed { index, it ->
                        if (it.content != null) {
                            item {
                                BasicTextField(
                                    value = it.content,
                                    onValueChange = { newValue ->
                                        viewModel.updateNote(noteID, index, newValue)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 4.dp, end = 4.dp),
                                    textStyle = TextStyle.Default.copy(fontSize = 16.sp)
                                )
                            }
                        } else if (it.bitmap != null) {
                            item {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        bitmap = it.bitmap.asImageBitmap(),
                                        contentDescription = "Image from notes",
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .fillMaxWidth(0.8F)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    modifier = Modifier
                        .width(235.dp)
                        .padding(4.dp),
                    onClick = {
                        viewModel.onTextToSpeech(
                            note?.items?.joinToString { uiNoteItem -> uiNoteItem.content ?: "" }
                                ?: "No content to read")
                        ttsButtonText = if (viewModel.isSpeaking) {
                            "Stop Reading Notes    "
                        } else {
                            "Read Summarized Notes"
                        }
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
                    Text(text = ttsButtonText)
                }
            }
        }
    }
}

@Preview
@Composable
fun SingleNoteScreenPreview() {
    SingleNoteScreen(1, navController = rememberNavController())
}