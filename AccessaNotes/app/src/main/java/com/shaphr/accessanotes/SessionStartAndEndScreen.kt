package com.shaphr.accessanotes


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

//UI screen for starting and stopping a session
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionStartAndEndScreen(){
    Scaffold(
        //top settings navigation bar
        topBar = { TopNav("TEST")},
        // TODO
        content = { padding ->
            Text(modifier = Modifier.padding(padding), text = "Start and End Session Screen")
        }
    )
}