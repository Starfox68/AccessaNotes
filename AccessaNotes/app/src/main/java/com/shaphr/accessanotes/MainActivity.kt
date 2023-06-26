package com.shaphr.accessanotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shaphr.accessanotes.ui.screens.LiveRecordingScreen
import com.shaphr.accessanotes.ui.screens.NoteRepositoryScreen
import com.shaphr.accessanotes.ui.screens.SessionStartAndEndScreen
import com.shaphr.accessanotes.ui.screens.SingleNoteScreen
import com.shaphr.accessanotes.ui.theme.AccessaNotesTheme
import dagger.hilt.android.AndroidEntryPoint

//sealed class idea came from this tutorial:
//https://www.youtube.com/watch?v=hGg0HjcoP9w
//UI screens with corresponding routes for navigation
sealed class Destination(val route: String){
    object NoteRepositoryScreen: Destination("noteRepositoryScreen")
    object LiveRecordingScreen: Destination("liveRecordingScreen")
    object SessionStartAndEndScreen: Destination("sessionStartAndEndScreen")
    object SingleNoteScreen: Destination("singleNoteScreen/{noteID}") {
        //create a new route with the given note ID
        fun createRoute(noteID: Int): String {
            return "singleNoteScreen/$noteID"
        }
    }
}


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AccessaNotesTheme {
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
}

//Functionality for navigating to different UI pages
@Composable
fun NavigationAppHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "noteRepositoryScreen") {
        composable(Destination.NoteRepositoryScreen.route) { NoteRepositoryScreen(navController) }
        composable(Destination.LiveRecordingScreen.route) { LiveRecordingScreen() }
        composable(Destination.SessionStartAndEndScreen.route) { SessionStartAndEndScreen(navController) }
        composable(Destination.SingleNoteScreen.route) { navBackStackEntry ->
            //get noteID from within the route
            val noteID = navBackStackEntry.arguments?.getString("noteID")?.toInt()
            if (noteID != null) {
                SingleNoteScreen(noteID)
            }
        }
    }

}