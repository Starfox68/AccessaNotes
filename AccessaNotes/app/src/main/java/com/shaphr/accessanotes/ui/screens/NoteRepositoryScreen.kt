package com.shaphr.accessanotes.ui.screens

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.shaphr.accessanotes.AuthResultContract
import com.shaphr.accessanotes.Destination
import com.shaphr.accessanotes.R
import com.shaphr.accessanotes.ui.components.BottomNavBar
import com.shaphr.accessanotes.ui.components.SignInButton
import com.shaphr.accessanotes.ui.viewmodels.NoteRepositoryViewModel


//search bar at the top of the screen if time permits

//each note has title, date, and share button


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteRepositoryScreen(
    navController: NavHostController,
    viewModel: NoteRepositoryViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    
    var text by remember { mutableStateOf<String?>(null) }
    val signInRequestCode = 1

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) { task ->
            try {
                val account = task?.getResult(ApiException::class.java)
                if (account == null) {
                    text = "Google sign in failed"
                } else {
                    val driveInstance = getDriveService(context)
                    if (driveInstance == null){
                        text = "Drive sign in failed"
                    }else{
                        uploadFileToGDrive(context)
//                        Log.d("HI","You're in drive with this login:")
//                        Log.d("HI", account.email!!)
//                        Log.d("HI", account.displayName!!)
                    }

                }
            } catch (e: ApiException) {
                text = "Google sign in failed"
            }
        }

    val notes = viewModel.notes.collectAsState().value
    var isLoading by remember { mutableStateOf(false) }

    Scaffold (
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(0.dp,40.dp,0.dp,30.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center ) {
                Text(text="All Notes", fontSize = 40.sp, maxLines = 1)
            }
                 },
        content = { padding ->
            LazyColumn(modifier = Modifier.padding(padding)) {
                notes.forEach { note ->
                    item(note.id) {
                        val paddingModifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .defaultMinSize(20.dp, 50.dp)
                            .height(IntrinsicSize.Min)
                            .clickable {
                                Log.d("TEST", "id is ${note.id}")
                                navController.navigate(
                                    Destination.SingleNoteScreen.createRoute(
                                        note.id
                                    )
                                )
                            }
                        Card(shape = RoundedCornerShape(3.dp), modifier = paddingModifier, elevation = CardDefaults.cardElevation(10.dp), ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp)) {
                                Text(note.title, color = Color.Black)
                                    Text(note.date.toString(), color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.width(25.dp))
                                Divider(modifier = Modifier
                                    .fillMaxHeight()
                                    .width(1.dp))
                                Spacer(modifier = Modifier.weight(1f))
                                SignInButton(
                                    text = "Share to Drive",
                                    loadingText = "Signing in...",
                                    isLoading = isLoading,
                                    icon = painterResource(id = R.drawable.ic_google_logo_small),
                                    onClick = { authResultLauncher.launch(signInRequestCode) }
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomNavBar(navController)
        }
    )
}

//reference https://www.section.io/engineering-education/backup-services-with-google-drive-api-in-android/
private fun getDriveService(context: Context): Drive? {
    GoogleSignIn.getLastSignedInAccount(context)?.let { googleAccount ->
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_FILE)
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
fun uploadFileToGDrive(context: Context) {
    Log.d("Hi", "Uploading file")
//    getDriveService(context)?.let { googleDriveService ->
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val localFileDirectory = File(getExternalFilesDir("backup")!!.toURI())
//                val actualFile = File("${localFileDirectory}/FILE_NAME_BACKUP")
//                val gFile = com.google.api.services.drive.model.File()
//                gFile.name = actualFile.name
//                val fileContent = FileContent("text/plain", actualFile)
//                googleDriveService.Files().create(gFile, fileContent).execute()
//            } catch (exception: Exception) {
//                exception.printStackTrace()
//            }
//        }
//    }
}
