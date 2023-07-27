package com.shaphr.accessanotes.ui.screens

import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.shaphr.accessanotes.Destination
import com.shaphr.accessanotes.R
import com.shaphr.accessanotes.ui.components.TopScaffold
import com.shaphr.accessanotes.ui.viewmodels.StartAndEndScreenViewModel
import kotlinx.coroutines.launch
import java.net.URLEncoder

@Composable
fun SessionStartAndEndScreen(navController: NavHostController, viewModel: StartAndEndScreenViewModel = hiltViewModel()) {
    val canStart = viewModel.canStart.collectAsState().value
    val title = viewModel.title.collectAsState().value
    var prompt = viewModel.prompt.collectAsState().value
    var audioFile = viewModel.audioFile.collectAsState().value
    var fileText = viewModel.fileText.collectAsState().value
    val context = LocalContext.current

    TopScaffold(text = "Start Recording Session", navController = navController) { padding ->
        SessionStartScreen(
            padding = padding,
            onStartClick = {
                if (audioFile == Uri.EMPTY) {
                    println("Audio file was not null")
                    val mediaPlayer = MediaPlayer.create(context, R.raw.recording_started)
                    mediaPlayer.setVolume(1F,1F)
                    mediaPlayer.start()
                }

                println("Clicked Start")
                println("File text: $fileText")
                if (prompt.isBlank()) {
                    println("Prompt was blank, using default")
                    prompt = "Summarize the following transcript as nested bullet points, capturing the main ideas"
                }
                if (!fileText.isBlank()) {
                    prompt += "\n\nBut before I give you the transcript use the below text for preliminary context to improve your summary and incorporate it with same formatting:\n$fileText"
                }
                fileText = ""
                val uriStr = URLEncoder.encode(audioFile.toString(), "UTF-8")
                if (uriStr != "" || uriStr != null) {
                    navController.navigate(Destination.LiveRecordingScreen.createRoute(prompt, uriStr))
                } else {
                    navController.navigate(Destination.LiveRecordingScreen.createRoute(prompt, "0"))
                }
            },
            canStart = canStart,
            title = title,
            setName = viewModel::setTitle,
            setPrompt = viewModel::setPrompt,
            viewModel = viewModel
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionStartScreen(
    padding: PaddingValues,
    onStartClick: () -> Unit,
    canStart: Boolean,
    title: String,
    setName: (String) -> Unit,
    setPrompt: (String) -> Unit,
    viewModel: StartAndEndScreenViewModel
) {
    var dropdown1Expanded by remember { mutableStateOf(false) }
    var dropdown2Expanded by remember { mutableStateOf(false) }
    var promptPurpose by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding(),
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Text("Session Title:", style = MaterialTheme.typography.titleMedium)
        ShowSessionTitle(title, setName)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Prompt Purpose:", style = MaterialTheme.typography.titleMedium)
        TextField(
            value = promptPurpose,
            onValueChange = { newText ->
                promptPurpose = newText
                setPrompt(promptPurpose.text)
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp, max = 200.dp)
                .testTag("textField2"),
            singleLine = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))
        ShowUploadButton(viewModel)
        ShowUploadMP3Button(viewModel)

        Spacer(modifier = Modifier.height(16.dp))
        ShowStartButton("Start", canStart, onStartClick)
    }
}

@Composable
fun ShowUploadMP3Button(viewModel: StartAndEndScreenViewModel) {
    val context = LocalContext.current
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    val rememberLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedUri = it
            viewModel.setAudioFile(it)
        }
    }
    var clickState by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                println("Clicked Upload MP3")
                clickState = !clickState
            }
        ) {
            Text("Upload Audio", style = MaterialTheme.typography.bodyMedium)
        }
        selectedUri?.let { uri ->
            val filename = DocumentFile.fromSingleUri(context, uri)?.name
            filename?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Uploaded")
                    Text(it, Modifier.padding(start = 4.dp))
                }
            }
        }
    }

    LaunchedEffect(clickState) {
        if (clickState) {
            rememberLauncher.launch("audio/mpeg")
        }
    }
}

@Composable
fun ShowUploadButton(viewModel: StartAndEndScreenViewModel) {
    val context = LocalContext.current
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val rememberLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Launch a new coroutine to call getFileContext
            coroutineScope.launch {
                viewModel.getFileContext(it)
            }
            selectedUri = it
        }
    }

    var clickState by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                println("Clicked Upload")
                clickState = !clickState
            }
        ) {
            Text("Upload Relevant Files", style = MaterialTheme.typography.bodyMedium)
        }

        // Show filename after uploading
        selectedUri?.let { uri ->
            val filename = DocumentFile.fromSingleUri(context, uri)?.name
            filename?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Uploaded")
                    Text(it, Modifier.padding(start = 4.dp))
                }
            }
        }
    }

    LaunchedEffect(clickState) {
        if (clickState) {
            rememberLauncher.launch("*/*") // replace with "*/*" to allow all file types
        }
    }
}

@Composable
fun ShowStartButton(text: String, canStart: Boolean, onStartClick: () -> Unit){
    Button(
        onClick = onStartClick,
        enabled = canStart
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowSessionTitle(title: String, setName: (String) -> Unit){
    TextField(
        value = title,
        onValueChange = {
            setName(it)
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("textField1")
    )
}
@Preview
@Composable
fun SessionStartAndEndScreenPreview() {
    SessionStartAndEndScreen(navController = rememberNavController())
}