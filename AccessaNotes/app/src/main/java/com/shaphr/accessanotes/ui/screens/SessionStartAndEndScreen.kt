package com.shaphr.accessanotes.ui.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.shaphr.accessanotes.Destination
import com.shaphr.accessanotes.ui.components.TopNav

@Composable
fun SessionStartAndEndScreen(navController: NavHostController) {
    SessionStartScreen(
        onStartClick = {
            navController.navigate(Destination.LiveRecordingScreen.route)
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionStartScreen(
    onStartClick: () -> Unit
) {
    var dropdown1Expanded by remember { mutableStateOf(false) }
    var dropdown2Expanded by remember { mutableStateOf(false) }
    var sessionTitleText by remember { mutableStateOf(TextFieldValue("")) }
    var promptPurpose by remember { mutableStateOf(TextFieldValue("")) }

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
                    value = sessionTitleText,
                    onValueChange = { newText -> sessionTitleText = newText  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("textField1")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Prompt Purpose:")
                TextField(
                    value = promptPurpose,
                    onValueChange = { newText -> promptPurpose = newText },
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
                    onClick = onStartClick,
                ) {
                    Text("Start")
                }
            }
        }
    )
}
@Preview
@Composable
fun SessionStartAndEndScreenPreview() {
    SessionStartAndEndScreen(navController = rememberNavController())
}