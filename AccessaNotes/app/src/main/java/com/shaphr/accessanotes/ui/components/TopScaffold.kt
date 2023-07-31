package com.shaphr.accessanotes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

//Scaffold layout for most screens
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopScaffold(
    text: String,
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold (
        //use topBar for title of the screen
        topBar = {
            Row(
                //Align text vertically and horizontally
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 40.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        },
        //pass in composable as content for the screen
        content = content,
        //standard bottom nav bar across all screens
        bottomBar = {
            BottomNavBar(navController)
        }
    )
}