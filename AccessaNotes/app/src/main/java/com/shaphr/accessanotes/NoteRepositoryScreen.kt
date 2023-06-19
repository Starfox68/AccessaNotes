package com.shaphr.accessanotes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

//UI screen for viewing all notes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteRepositoryScreen(navController: NavHostController){
    //will come from view model eventually
    //TODO
    val listOfNotes = MutableList(100){ it }

    Scaffold(
        //top navigation settings bar
        topBar = { TopNav()},
        //list of notes that can be clicked
        content = { padding ->
            FloatingActionButton(navController)
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(listOfNotes) { singleNote ->
                    Row(modifier = Modifier
                        //create a new route with the specified noteID
                        .clickable {
                            navController.navigate(
                                Destination.SingleNoteScreen.createRoute(
                                    singleNote
                                )
                            )
                        }
                    ) {
                        Text("This is note number: $singleNote")
                    }
                }
            }
        }
    )






}