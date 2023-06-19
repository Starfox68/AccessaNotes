package com.shaphr.accessanotes

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

//UI screen for viewing a single saved note
@Composable
fun SingleNoteScreen(noteID: Int, navController: NavHostController){
    //TODO
    Text(text = "This is note with ID: $noteID")
    FloatingActionButton(navController)
}