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

@Composable
fun HomeScreen(navController: NavHostController){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(text = "Home Screen")
        Button(onClick = { navController.navigate(Destination.LiveRecordingScreen.route) }){
            Text(text = "to LiveRecordingScreen")
        }
        Button(onClick = { navController.navigate(Destination.SessionStartAndEndScreen.route) }){
            Text(text = "to SessionStartAndEndScreen")
        }
        Button(onClick = { navController.navigate(Destination.SingleNoteScreen.route) }){
            Text(text = "to SingleNoteScreen")
        }
    }
}