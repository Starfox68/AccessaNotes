package com.shaphr.accessanotes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shaphr.accessanotes.ui.screens.AccountScreen
import com.shaphr.accessanotes.ui.screens.CameraScreen
import com.shaphr.accessanotes.ui.screens.LiveRecordingScreen
import com.shaphr.accessanotes.ui.screens.NoteRepositoryScreen
import com.shaphr.accessanotes.ui.screens.SessionStartAndEndScreen
import com.shaphr.accessanotes.ui.screens.SingleNoteScreen
import com.shaphr.accessanotes.ui.theme.AccessaNotesTheme
import com.shaphr.accessanotes.ui.viewmodels.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

// sealed class idea came from this tutorial:
// https://www.youtube.com/watch?v=hGg0HjcoP9w
// UI screens with corresponding routes for navigation
sealed class Destination(val route: String) {
    object NoteRepositoryScreen : Destination("noteRepositoryScreen")
    object LiveRecordingScreen : Destination("liveRecordingScreen/{prompt}/{existingAudio}") {
        fun createRoute(prompt: String, existingAudio: String): String {
            return "liveRecordingScreen/$prompt/${existingAudio}"
        }
    }
    object SessionStartAndEndScreen : Destination("sessionStartAndEndScreen")
    object SingleNoteScreen : Destination("singleNoteScreen/{noteID}") {
        fun createRoute(noteID: Int): String {
            return "singleNoteScreen/$noteID"
        }
    }

    object CameraScreen : Destination("cameraScreen")
    object AccountScreen : Destination("accountScreen")
}


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted.
                // This is just an example, might want to start recording from a different place
//                TranscriptionClient().startRecording()
            } else {
                // Permission is denied, display a toast
                Toast.makeText(this, "Permission denied, can't record audio", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()

            AccessaNotesTheme(
                isColourBlind = themeViewModel.colourFlow.collectAsState(initial = false).value,
                isLargeFont = themeViewModel.fontFlow.collectAsState(initial = false).value,
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //main navigation controller
                    val navController = rememberNavController()
                    NavigationAppHost(navController = navController)
                }
            }
        }
    }

//    override fun onDestroy() {
          // for cleaning up resources
//        super.onDestroy()
//        transcriptionClient.stopRecording()
//    }
}

//Functionality for navigating to different UI pages
@Composable
fun NavigationAppHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "noteRepositoryScreen") {
        composable(Destination.NoteRepositoryScreen.route) { NoteRepositoryScreen(navController) }
        composable(Destination.LiveRecordingScreen.route) { navBackStackEntry ->
            LiveRecordingScreen(navBackStackEntry, navController)
        }
        composable(Destination.AccountScreen.route) { AccountScreen(navController) }
        composable(Destination.SessionStartAndEndScreen.route) {
            SessionStartAndEndScreen(
                navController
            )
        }
        composable(Destination.SingleNoteScreen.route) { navBackStackEntry ->
            //get noteID from within the route
            val noteID = navBackStackEntry.arguments?.getString("noteID")?.toInt()
            if (noteID != null) {
                SingleNoteScreen(noteID, navController)
            }
        }
        composable(Destination.CameraScreen.route) { CameraScreen(navController) }
    }
}