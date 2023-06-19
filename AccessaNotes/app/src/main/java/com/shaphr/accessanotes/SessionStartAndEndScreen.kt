package com.shaphr.accessanotes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHost
import androidx.navigation.NavHostController

@Composable
fun SessionStartAndEndScreen(navController: NavHostController){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(text = "Session Start and End Screen")
        Button(onClick = { navController.navigate(Destination.HomeScreen.route) }){
            Text(text = "to HomeScreen")
        }
        Button(onClick = { navController.navigate(Destination.LiveRecordingScreen.route) }){
            Text(text = "to LiveRecordingScreen")
        }
        Button(onClick = { navController.navigate(Destination.SingleNoteScreen.route) }){
            Text(text = "to SingleNoteScreen")
        }
    }
}