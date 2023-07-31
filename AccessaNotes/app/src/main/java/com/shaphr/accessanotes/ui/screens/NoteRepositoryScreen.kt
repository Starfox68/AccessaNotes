package com.shaphr.accessanotes.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.shaphr.accessanotes.AuthResultContract
import com.shaphr.accessanotes.Destination
import com.shaphr.accessanotes.R
import com.shaphr.accessanotes.data.models.UiNote
import com.shaphr.accessanotes.ui.components.SignInButton
import com.shaphr.accessanotes.ui.components.TopScaffold
import com.shaphr.accessanotes.ui.viewmodels.DialogState
import com.shaphr.accessanotes.ui.viewmodels.FileFormat
import com.shaphr.accessanotes.ui.viewmodels.NoteRepositoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

//Screen for showing all saved user notes
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteRepositoryScreen(
    navController: NavHostController,
    viewModel: NoteRepositoryViewModel = hiltViewModel(),
) {
    //collect values from the viewModel as needed
    val notes = viewModel.notes.collectAsState(initial = emptyList()).value
    val fileFormat = viewModel.fileFormat.collectAsState().value
    val dialogState = viewModel.dialogState.collectAsState().value
    val selectedNotes = viewModel.selectedNotes.collectAsState().value

    //Google sign-in + google auth launcher
    val context = LocalContext.current
    val signInRequestCode = 1

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) { task ->
            try {
                //launch account sign-in
                val account = task?.getResult(ApiException::class.java)
                if (account == null) {
                    Log.d("drive", "account is null")
                } else {
                    //launch drive Auth and upload file
                    uploadFilesToGDrive(context, viewModel.getSelectedNotes(), fileFormat)
                }
            } catch (e: ApiException) {
                Log.d("drive", "sign in failed")
            }
        }

    //show file format pop-up after user clicks the button
    if (viewModel.dialogState.collectAsState().value != DialogState.CLOSED) {
        FileFormatDialog(
            onDismiss = viewModel::onDialogClose,
            onConfirm = {
                viewModel.onDialogConfirm(it)
                if (dialogState == DialogState.SHARE_OPEN) {
                    authResultLauncher.launch(signInRequestCode)
                }
                viewModel.onDialogClose()
            }
        )
    }

    //use scaffold layout for screen
    TopScaffold(text = "All Notes", navController = navController) { padding ->
        Divider(modifier = Modifier.padding(16.dp))
        //show image of confused man if there are no saved notes
        if (notes.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.confusedman),
                        contentDescription = "No Notes",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "It looks like you have no notes. Create a session to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            //Show notes
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                //Top row for sharing, deleting, and checkbox functionality
                OptionRow(
                    isVisible = true,
                    onDownloadClick = { viewModel.showDialog(DialogState.DOWNLOAD_OPEN) },
                    onDeleteClick = viewModel::onDeleteClick,
                    onShareClick = { viewModel.showDialog(DialogState.SHARE_OPEN) },
                    isAllSelected = viewModel.allSelected.collectAsState().value,
                    onAllSelect = viewModel::onAllSelect,
                    isClickable = viewModel.selectedNotes.value.isNotEmpty()
                )
                //render each note as a card aligned vertically
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                    notes.forEach { note ->
                        item(note.id) {
                            //custom note component
                            NoteCard(
                                note = note,
                                isSelected = note.id in selectedNotes,
                                onSelect = viewModel::onNoteSelect,
                                //create route for note and navigate to it
                                onClick = {
                                    navController.navigate(
                                        Destination.SingleNoteScreen.createRoute(note.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

//Custom row component which has:
// google drive sharing button, delete button, download button, and checkbox to select all notes
@Composable
fun OptionRow(
    isVisible: Boolean,
    onDownloadClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
    isAllSelected: Boolean,
    onAllSelect: (Boolean) -> Unit,
    isClickable: Boolean
) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(horizontal = 16.dp)) {
        //animate row to fade in or out when it's not selected
        AnimatedVisibility(visible = isVisible, enter = fadeIn(), exit = fadeOut()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SignInButton(
                    text = "Share to Drive",
                    icon = painterResource(id = R.drawable.ic_google_logo_small),
                    onClick = onShareClick,
                    clickable = isClickable
                )
                IconButton(onClick = onDownloadClick, enabled = isClickable) {
                    Icon(
                        Icons.Outlined.Download,
                        contentDescription = "Download"
                    )
                }
                IconButton(onClick = onDeleteClick, enabled = isClickable) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Delete"
                    )
                }

            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Checkbox(checked = isAllSelected, onCheckedChange = onAllSelect)
    }
}

//render notes as cards with functionality for selecting and clicking on notes
@Composable
fun NoteCard(
    note: UiNote,
    isSelected: Boolean,
    onSelect: (Boolean, Int) -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        //align all content horizontally in the note
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                //display title and date one above the other
                Text(note.title, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                Text(
                    note.date.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            //check box for selecting note
            Checkbox(
                checked = isSelected,
                onCheckedChange = { selected -> onSelect(selected, note.id) })
        }
    }
}

//Pop-up for selecting file format to download
@ExperimentalMaterial3Api
@Composable
fun FileFormatDialog(
    onDismiss: () -> Unit,
    onConfirm: (FileFormat) -> Unit
) {
    //types of supported files
    val options = listOf(FileFormat.PDF, FileFormat.DOCX, FileFormat.TXT)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(options[0]) }

    //Show dialogue if no options selected
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Select a file format",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Column(
                    modifier = Modifier.selectableGroup()
                ) {
                    Divider()
                    //List of file formats which can be clicked
                    options.forEach {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(it.text, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.weight(1f))
                            RadioButton(
                                selected = it == selectedOption,
                                onClick = { onOptionSelected(it) })
                        }
                        Divider()
                    }
                }
                //Cancel and confirm buttons for pop-up
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", style = MaterialTheme.typography.bodyMedium)
                    }
                    TextButton(onClick = { onConfirm(selectedOption) }) {
                        Text("Confirm", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

//reference https://www.section.io/engineering-education/backup-services-with-google-drive-api-in-android/
//function for making a new drive instance, given a signed-in google account
private fun getDriveService(context: Context): Drive? {
    //get last signed in google account
    GoogleSignIn.getLastSignedInAccount(context)?.let { googleAccount ->
        //get credentials with correct scopes
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE)
        )
        credential.selectedAccount = googleAccount.account!!
        //create drive instance
        return Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName(R.string.app_name.toString())
            .build()
    }
    //return null if it fails
    return null
}

//reference https://www.section.io/engineering-education/backup-services-with-google-drive-api-in-android/
//function for uploading file to drive given a drive instance
fun uploadFilesToGDrive(context: Context, notes: List<UiNote>, fileFormat: FileFormat) {
    //get drive instance
    getDriveService(context)?.let { googleDriveService ->
        //format file depending on file type
        val docType = when (fileFormat) {
            FileFormat.DOCX -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            FileFormat.PDF -> "application/pdf"
            FileFormat.TXT -> "text/plain"
        }
        //go through notes in case multiple were selected
        for (note in notes) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    //find local file in downloads folder
                    val localFileDirectory =
                        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
                    val actualFile =
                        File("${localFileDirectory}/${note.title}.${fileFormat.text.lowercase()}")

                    //create google file version of local file
                    val gFile = com.google.api.services.drive.model.File()
                    gFile.name = actualFile.name

                    //upload file to drive
                    val fileContent = FileContent(docType, actualFile)
                    googleDriveService.Files().create(gFile, fileContent).execute()

                    //delete file from downloads folder
                    actualFile.delete()
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }
    }
}
