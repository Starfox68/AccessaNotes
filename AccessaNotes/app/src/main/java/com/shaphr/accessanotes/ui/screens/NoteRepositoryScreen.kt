package com.shaphr.accessanotes.ui.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.google.android.gms.common.api.ApiException
import com.shaphr.accessanotes.AuthResultContract
import com.shaphr.accessanotes.R
import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.ui.components.BottomNavBar
import com.shaphr.accessanotes.ui.components.SignInButton


//search bar at the top of the screen if time permits

//each note has title, date, and share button


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteRepositoryScreen(
    navController: NavHostController,
//    viewModel: NoteRepositoryViewModel = hiltViewModel(),
) {

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    var text by remember { mutableStateOf<String?>(null) }
    val signInRequestCode = 1

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) { task ->
            try {
                val account = task?.getResult(ApiException::class.java)
                if (account == null) {
                    text = "Google sign in failed"
                } else {
                    Log.d("HI", account.email!!)
                    Log.d("HI", account.displayName!!)
                }
            } catch (e: ApiException) {
                text = "Google sign in failed"
            }
        }


//    val notes = viewModel.notes.collectAsState().value
    val isLoading by remember { mutableStateOf(false) }

    val notes = listOf(
        Note("Title 1", "Test Note 1", id=0),
        Note("Title 2", "Test Note 2", id=1 ),
        Note("Title 3", "Test Note 2", id=2 )
    )

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
//                            .clickable {
////                                navController.navigate(
////                                    Destination.SingleNoteScreen.createRoute(
////                                        note.id
////                                    )
////                                )
//                            }
                        Card(shape = RoundedCornerShape(3.dp), modifier = paddingModifier, elevation = CardDefaults.cardElevation(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp)) {
                                Text(note.title, color = Color.Black)
                                    Text(note.date.toString(), color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.width(25.dp))
                                Divider(modifier = Modifier
                                    .fillMaxHeight()  //fill the max height
                                    .width(1.dp))
                                Spacer(modifier = Modifier.weight(1f))
                                SignInButton(
                                    text = "Share to Drive",
                                    loadingText = "Signing in...",
                                    isLoading = isLoading,
                                    icon = painterResource(id = R.drawable.ic_google_logo),
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

//private fun getDriveService(): Drive? {
//    GoogleSignIn.getLastSignedInAccount(this)?.let { googleAccount ->
//        val credential = GoogleAccountCredential.usingOAuth2(
//            this, listOf(DriveScopes.DRIVE_FILE)
//        )
//        credential.selectedAccount = googleAccount.account!!
//        return Drive.Builder(
//            AndroidHttp.newCompatibleTransport(),
//            JacksonFactory.getDefaultInstance(),
//            credential
//        )
//            .setApplicationName(R.string.app_name.toString())
//            .build()
//    }
//    return null
//}
