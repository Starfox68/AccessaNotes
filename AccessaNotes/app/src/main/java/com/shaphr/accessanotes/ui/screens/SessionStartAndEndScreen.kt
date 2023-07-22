package com.shaphr.accessanotes.ui.screens


import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.shaphr.accessanotes.Destination
import com.shaphr.accessanotes.R
import com.shaphr.accessanotes.ui.components.BottomNavBar
import com.shaphr.accessanotes.ui.components.TopNav
import com.shaphr.accessanotes.ui.viewmodels.StartAndEndScreenViewModel

@Composable
fun SessionStartAndEndScreen(navController: NavHostController, viewModel: StartAndEndScreenViewModel = hiltViewModel()) {
    val canStart = viewModel.canStart.collectAsState().value
    val title = viewModel.title.collectAsState().value
    var prompt = viewModel.prompt.collectAsState().value
    var fileText = viewModel.fileText.collectAsState().value
    val context = LocalContext.current

    SessionStartScreen(
        onStartClick = {
            val mediaPlayer = MediaPlayer.create(context, R.raw.recording_started)
            mediaPlayer.setVolume(1F,1F)
            mediaPlayer.start()

            println("Clicked Start")
            println("File text: $fileText")
            if (prompt.isBlank()) {
                println("Prompt was blank, using default")
                prompt = "Summarize the following transcript as nested bullet points, capturing the main ideas"
            }
            if (!fileText.isBlank()) {
                prompt += "\n\nBut before the transcript also use this text which gives some more context to improve your summary and incorporate it with same formatting:\n $fileText"
            }
            fileText = ""
            navController.navigate(Destination.LiveRecordingScreen.createRoute(prompt))
        },
        canStart = canStart,
        title = title,
        setName = viewModel::setTitle,
        setPrompt = viewModel::setPrompt,
        viewModel = viewModel,
        navController = navController
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionStartScreen(
    onStartClick: () -> Unit,
    canStart: Boolean,
    title: String,
    setName: (String) -> Unit,
    setPrompt: (String) -> Unit,
    viewModel: StartAndEndScreenViewModel,
    navController: NavHostController
) {
    var dropdown1Expanded by remember { mutableStateOf(false) }
    var dropdown2Expanded by remember { mutableStateOf(false) }
    var promptPurpose by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(0.dp,40.dp,0.dp,40.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center ) {
                Text(text="Start Recording Session", fontSize = 30.sp, maxLines = 1)
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                Text("Session Title:")
                ShowSessionTitle(title, setName)

                Spacer(modifier = Modifier.height(16.dp))

                Text("Prompt Purpose:")
                TextField(
                    value = promptPurpose,
                    onValueChange = { newText ->
                        promptPurpose = newText
                        setPrompt(promptPurpose.text)
                                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 200.dp)
                        .testTag("textField2"),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Note Style Template:")
                DropdownMenu(
                    expanded = dropdown1Expanded,
                    onDismissRequest = { dropdown1Expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dropdown1")
                ) {
//                    DropdownMenuItem(onClick = { /* Handle option 1 selection */ }) {
//                        Text("Option 1")
//                    }
//                    DropdownMenuItem(onClick = { /* Handle option 2 selection */ }) {
//                        Text("Option 2")
//                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Export Format:")
                DropdownMenu(
                    expanded = dropdown2Expanded,
                    onDismissRequest = { dropdown2Expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dropdown2")
                ) {
//                    DropdownMenuItem(onClick = { /* Handle option 1 selection */ }) {
//                        Text("Option 1")
//                    }
//                    DropdownMenuItem(onClick = { /* Handle option 2 selection */ }) {
//                        Text("Option 2")
//                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                ShowUploadButton(viewModel)

                ShowStartButton(canStart, onStartClick)
            }
        },
        bottomBar = { BottomNavBar(navController)}
    )
}

@Composable
fun ShowUploadButton(viewModel: StartAndEndScreenViewModel) {
    val context = LocalContext.current
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val rememberLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.readFile(it)
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
                clickState = !clickState
            }
        ) {
            Text("Upload Relevant Files")
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
            rememberLauncher.launch("text/*") // replace with "*/*" to allow all file types
        }
    }
}

@Composable
fun ShowStartButton(canStart: Boolean, onStartClick: () -> Unit){
    Button(
        onClick = onStartClick,
        enabled = canStart
    ) {
        Text("Start")
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