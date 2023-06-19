package com.shaphr.accessanotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

//UI screen for when a session is currently underway
@Composable
fun LiveRecordingScreen(navController: NavHostController){
    //TODO
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(text = "Live Recording Screen")
        Button(onClick = { navController.navigate(Destination.NoteRepositoryScreen.route) }){
            Text(text = "to noteRepositoryScreen")
        }
        Button(onClick = { navController.navigate(Destination.SessionStartAndEndScreen.route) }){
            Text(text = "to sessionStartAndEndScreen")
        }
        Button(onClick = { navController.navigate(Destination.SingleNoteScreen.route) }){
            Text(text = "to SingleNoteScreen")
        }
    }
}