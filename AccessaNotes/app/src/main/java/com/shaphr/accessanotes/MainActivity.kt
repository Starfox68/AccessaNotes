package com.shaphr.accessanotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shaphr.accessanotes.ui.theme.AccessaNotesTheme
import dagger.hilt.android.AndroidEntryPoint


sealed class Destination(val route: String){
    object HomeScreen: Destination("homeScreen")
    object LiveRecordingScreen: Destination("liveRecordingScreen")
    object SessionStartAndEndScreen: Destination("sessionStartAndEndScreen")
    object SingleNoteScreen: Destination("singleNoteScreen")
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
                    val navController = rememberNavController()
                    NavigationAppHost(navController = navController)
                }
            }
        }
    }
}

@Composable
fun NavigationAppHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "homeScreen"){
        composable(Destination.HomeScreen.route) { HomeScreen(navController) }
        composable(Destination.LiveRecordingScreen.route) { LiveRecordingScreen(navController) }
        composable(Destination.SessionStartAndEndScreen.route) { SessionStartAndEndScreen(navController) }
        composable(Destination.SingleNoteScreen.route) { SingleNoteScreen(navController) }
    }

}