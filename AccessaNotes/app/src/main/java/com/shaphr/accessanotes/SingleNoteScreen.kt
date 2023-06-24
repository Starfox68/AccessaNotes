package com.shaphr.accessanotes

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

//UI screen for viewing a single saved note


@Composable
fun SingleNoteScreen(noteID: Int, navController: NavHostController){
    //TODO
    topBar()
    // Text(text = "This is note with ID: $noteID")
    FloatingActionButton(navController)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topBar() {
    var dropdown1Expanded by remember { mutableStateOf(false) }
    var dropdown2Expanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = { TopNav("Record Session") },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                Text("Session Title:")
                TextField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("textField1")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Prompt Purpose:")
                TextField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 200.dp)
                        .testTag("textField2"),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Note Style Template:")
                DropdownMenu(
                    expanded = dropdown1Expanded,
                    onDismissRequest = { dropdown1Expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dropdown1")
                ) {
//                    DropdownMenuItem(onClick = { /* Handle option 1 selection */ }) {
//                        Text("Option 1")
//                    }
//                    DropdownMenuItem(onClick = { /* Handle option 2 selection */ }) {
//                        Text("Option 2")
//                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Export Format:")
                DropdownMenu(
                    expanded = dropdown2Expanded,
                    onDismissRequest = { dropdown2Expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dropdown2")
                ) {
//                    DropdownMenuItem(onClick = { /* Handle option 1 selection */ }) {
//                        Text("Option 1")
//                    }
//                    DropdownMenuItem(onClick = { /* Handle option 2 selection */ }) {
//                        Text("Option 2")
//                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                    Button(
                        onClick = { /* Handle button 1 click */ },

                    ) {
                        Text("Upload Relevant Files")
                    }

                    Button(
                        onClick = { /* Handle button 2 click */ },
                    ) {
                        Text("Start")
                    }
                }

        }
    )
}
@Preview
@Composable
fun SingleNoteScreenPreview() {
    SingleNoteScreen(noteID = 1, navController = rememberNavController())
}