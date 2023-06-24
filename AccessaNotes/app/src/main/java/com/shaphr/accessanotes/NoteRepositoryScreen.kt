package com.shaphr.accessanotes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline

//UI screen for viewing all notes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteRepositoryScreen(navController: NavHostController){
    //will come from view model eventually
    //TODO
    val listOfNotes = MutableList(100){ it }

    Scaffold(
        //top navigation settings bar
        topBar = { TopNav("All Saved Notes")},
//        topBar = {
//            TopAppBar(
//                title = { Text(text = "All Saved Notes") }
//            )
//        },
        //list of notes that can be clicked
        content = { padding ->
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(listOfNotes) { singleNote ->
                    Divider()
                    ListItem(headlineText = { Text("Chemistry - Lecture # $singleNote") },
                        supportingText = { Text("Date Created: 2022/04/$singleNote") },
                        modifier = Modifier.clickable{navController.navigate(Destination.SingleNoteScreen.createRoute(singleNote))})
                }
            }
            FloatingActionButton(navController)
        }
    )






}