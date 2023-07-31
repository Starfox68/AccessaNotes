package com.shaphr.accessanotes.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shaphr.accessanotes.Destination

//Floating Action Button for starting a new recording session
@Composable
fun FloatingActionButton(navController: NavHostController){
    Box(modifier = Modifier.fillMaxSize()){
        ExtendedFloatingActionButton(
            //styling
            modifier = Modifier
                .padding(20.dp)
                //align in the bottom right corner
                .align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary,
            //on click go to session start and end screen
            onClick = { navController.navigate(Destination.SessionStartAndEndScreen.route) }
        ) {
            Text(text = "New Session")
            Icon(Icons.Filled.Add, "Add")
        }
    }
}