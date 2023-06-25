package com.shaphr.accessanotes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shaphr.accessanotes.Destination

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val scroll = rememberScrollState(0)
    var text1 by remember { mutableStateOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Nibh sed pulvinar proin gravida hendrerit. Eleifend quam adipiscing vitae proin. Nec sagittis aliquam malesuada bibendum arcu vitae elementum. Arcu bibendum at varius vel pharetra vel turpis. Ut tellus elementum sagittis vitae et leo. Consequat ac felis donec et odio pellentesque diam. Condimentum id venenatis a condimentum vitae sapien pellentesque. Nibh nisl condimentum id venenatis a. Magna fermentum iaculis eu non diam phasellus. Tincidunt lobortis feugiat vivamus at augue eget arcu. Vitae turpis massa sed elementum tempus. Magnis dis parturient montes nascetur ridiculus mus.\n" +
            "\n" +
            "Augue interdum velit euismod in pellentesque massa. Ac placerat vestibulum lectus mauris. Viverra aliquet eget sit amet. Tincidunt vitae semper quis lectus nulla at. Sed augue lacus viverra vitae. Senectus et netus et malesuada fames ac. Duis ut diam quam nulla porttitor massa. Facilisi etiam dignissim diam quis enim. Condimentum vitae sapien pellentesque habitant morbi tristique. Nulla malesuada pellentesque elit eget gravida cum sociis natoque. Vitae auctor eu augue ut lectus arcu bibendum at. Id consectetur purus ut faucibus pulvinar elementum integer enim. Risus quis varius quam quisque id. Tincidunt arcu non sodales neque sodales ut etiam. Ultrices sagittis orci a scelerisque purus semper eget duis. Ipsum dolor sit amet consectetur adipiscing. Orci eu lobortis elementum nibh tellus molestie nunc non. Leo in vitae turpis massa sed elementum.\n" +
            "\n" +
            "Adipiscing tristique risus nec feugiat. Elit pellentesque habitant morbi tristique senectus et netus. Euismod in pellentesque massa placerat duis ultricies lacus sed. Justo eget magna fermentum iaculis eu non. Porttitor leo a diam sollicitudin tempor. Enim sed faucibus turpis in eu mi bibendum. Morbi tristique senectus et netus et malesuada fames ac turpis. Sit amet massa vitae tortor condimentum lacinia. Ut aliquam purus sit amet luctus venenatis. Euismod lacinia at quis risus sed vulputate odio ut. Leo in vitae turpis massa sed elementum tempus egestas sed. Convallis posuere morbi leo urna molestie at elementum. Est ante in nibh mauris cursus mattis molestie.") }
    var text2 by remember { mutableStateOf("") }
    val scrollableItems = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Record Session",
            modifier = Modifier.padding(16.dp)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            TextField(
                value = text1,
                onValueChange = { },
                maxLines=1,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            )


            TextField(
                value = text2,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Content to be scrollable goes here
            }

            Button(
                onClick = { /* Button 1 click action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Live Capture")
            }

            Button(
                onClick = { /* Button 2 click action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Stop")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LiveRecordingScreenPreview() {
    MyApp()
}
