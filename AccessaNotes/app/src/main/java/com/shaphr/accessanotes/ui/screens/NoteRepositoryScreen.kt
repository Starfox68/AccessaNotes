package com.shaphr.accessanotes.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.shaphr.accessanotes.Destination
import com.shaphr.accessanotes.ui.components.FloatingActionButton
import com.shaphr.accessanotes.ui.components.TopNav
import com.shaphr.accessanotes.ui.viewmodels.NoteRepositoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteRepositoryScreen(
    navController: NavHostController,
    viewModel: NoteRepositoryViewModel = hiltViewModel()
) {
    val notes = viewModel.notes.collectAsState().value

    Scaffold (
        topBar = { TopNav("All Saved Notes") },
        content = { padding ->
            LazyColumn(modifier = Modifier.padding(padding)) {
                notes.forEach { note ->
                    item(note.id) {
//                        Divider()
                        val paddingModifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clickable { navController.navigate(Destination.SingleNoteScreen.createRoute(note.id)) }
                        Card(shape = RoundedCornerShape(20.dp), modifier = paddingModifier) {
                            Column(modifier = paddingModifier) {
                                Text(note.title, color = Color.Black)
                                Text(note.date.toString(), color = Color.Gray)
                            }
                        }
                    }
                }
            }
            FloatingActionButton(navController)
        }
    )
}
