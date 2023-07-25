package com.shaphr.accessanotes.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.FileContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.shaphr.accessanotes.AuthResultContract
import com.shaphr.accessanotes.R
import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.ui.components.SignInButton
import com.shaphr.accessanotes.ui.components.TopScaffold
import com.shaphr.accessanotes.ui.viewmodels.NoteRepositoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


//search bar at the top of the screen if time permits

//each note has title, date, and share button


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteRepositoryScreen(
    navController: NavHostController,
    viewModel: NoteRepositoryViewModel = hiltViewModel(),
) {
    val notes = viewModel.notes.collectAsState().value
    var isLoading by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    val docConversionTypes = arrayOf("PDF", "DOCX", "TXT")
    var selectedText by remember { mutableStateOf(docConversionTypes[0]) }

    val selectedNotes = viewModel.selectedNotes.collectAsState().value

    TopScaffold(text = "All Notes", navController = navController) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
//            item{
//                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {expanded = !expanded } ) {
//                    TextField(
//                        value = selectedText,
////                        onValueChange = {viewModel.setDocType(selectedText)},
//                        onValueChange = { },
//                        textStyle = MaterialTheme.typography.bodyMedium,
//                        readOnly = true,
//                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//                        modifier = Modifier.menuAnchor()
//                    )
//
//                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//                        docConversionTypes.forEach { docType ->
//                            DropdownMenuItem(
//                                text = { Text(docType, style = MaterialTheme.typography.labelMedium) },
//                                onClick = {
//                                    viewModel.setDocType(docType)
//                                    selectedText = docType
//                                    expanded = false
//
//                                }
//                            )
//
//                        }
//
//                    }
//
//                }
//            }
            notes.forEach { note ->
                item(note.id) {
                    NoteCard (
                        note = note,
                        isSelected = note.id in selectedNotes,
                        onSelect = viewModel::onNoteSelect
                    )
                }
//                    val paddingModifier = Modifier
//                        .padding(10.dp)
//                        .fillMaxWidth()
//                        .defaultMinSize(20.dp, 50.dp)
//                        .height(IntrinsicSize.Min)
//                        .clickable {
//                            Log.d("TEST", "id is ${note.id}")
//                            navController.navigate(
//                                Destination.SingleNoteScreen.createRoute(
//                                    note.id
//                                )
//                            )
//                        }
//                    Card(shape = RoundedCornerShape(5.dp), modifier = paddingModifier, elevation = CardDefaults.cardElevation(4.dp)) {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Column(modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp)) {
//                                Text(note.title, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
//                                Text(note.date.toString(), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
//                            }
//                            Spacer(modifier = Modifier.width(25.dp))
//                            Divider(modifier = Modifier
//                                .fillMaxHeight()
//                                .width(1.dp))
//                            Spacer(modifier = Modifier.weight(1f))
//
//                            Button(
//                                onClick = {
//                                    viewModel.downloadNote(note)
//                                    Toast.makeText(context, "File Downloaded", Toast.LENGTH_LONG).show()
//                                },
//                                modifier = Modifier.width(65.dp) // Adjust the value to the desired width
//                            ) {
//                                Icon(
//                                    painterResource(id = R.drawable.baseline_file_download_black_24dp),
//                                    contentDescription = "Download Icon"
//                                )
//                                Spacer(modifier = Modifier.width(6.dp))
////                                    Text("Download")
//                            }
//
//                            Spacer(modifier = Modifier.weight(1f))
//
//                            SignInButton(
//                                text = "Share to Drive",
//                                loadingText = "Signing in...",
//                                isLoading = isLoading,
//                                icon = painterResource(id = R.drawable.ic_google_logo_small),
//                                onClick = { authResultLauncher.launch(signInRequestCode) }
//                            )
//                        }
//                    }
//                }
            }
        }
    }
}

@Composable
fun OptionRow(
    onDownloadClick: () -> Unit,
    isLoading: Boolean,
) {
    val context = LocalContext.current

    var text by remember { mutableStateOf<String?>(null) }

    val signInRequestCode = 1

    var noteToRemember: Note = Note("filler")

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) { task ->
            try {
                val account = task?.getResult(ApiException::class.java)
                if (account == null) {
                    Log.d("drive", "account is null")
                } else {
                    val driveInstance = getDriveService(context)
                    if (driveInstance == null){
                        Log.d("drive", "drive is null")
                    }else{
                        uploadFileToGDrive(context, noteToRemember, viewModel)
                    }
                }
            } catch (e: ApiException) {
                Log.d("drive", "sign in failed")
            }
        }

    Row {
        IconButton(onClick = onDownloadClick, modifier = Modifier.padding(16.dp)) {
            Icon(
                painterResource(id = R.drawable.baseline_file_download_black_24dp),
                contentDescription = "Download Icon"
            )
        }
        SignInButton(
            text = "Share to Drive",
            loadingText = "Signing in...",
            isLoading = isLoading,
            icon = painterResource(id = R.drawable.ic_google_logo_small),
            onClick = { authResultLauncher.launch(signInRequestCode) }
        )
    }
}

@Composable
fun NoteCard(
    note: Note,
    isSelected: Boolean,
    onSelect: (Boolean, Int) -> Unit,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(5.dp),
        elevation = if (isSelected) CardDefaults.cardElevation(10.dp) else CardDefaults.cardElevation(),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(note.title, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                Text(
                    note.date.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onSelect(!isSelected, note.id)}, modifier = Modifier.padding(4.dp)) {
                Icon(
                    if (isSelected) Icons.Outlined.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = null,
                )
            }
        }
    }
}

//reference https://www.section.io/engineering-education/backup-services-with-google-drive-api-in-android/
private fun getDriveService(context: Context): Drive? {
    GoogleSignIn.getLastSignedInAccount(context)?.let { googleAccount ->
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE)
        )
        credential.selectedAccount = googleAccount.account!!
        return Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName(R.string.app_name.toString())
            .build()
    }
    return null
}

//reference https://www.section.io/engineering-education/backup-services-with-google-drive-api-in-android/
fun uploadFileToGDrive(context: Context, note: Note, viewModel: NoteRepositoryViewModel) {
    getDriveService(context)?.let { googleDriveService ->
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val localFileDirectory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
                val actualFile = File("${localFileDirectory}/${note.title}.${viewModel.docType.value}")

                val gFile = com.google.api.services.drive.model.File()
                gFile.name = actualFile.name

                var docType = "application/pdf"
                if (viewModel.docType.value == "txt"){
                    docType = "text/plain"
                }else if (viewModel.docType.value == "docx"){
                    docType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                }

                val fileContent = FileContent(docType, actualFile)
                googleDriveService.Files().create(gFile, fileContent).execute()

                actualFile.delete()

            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }
}
