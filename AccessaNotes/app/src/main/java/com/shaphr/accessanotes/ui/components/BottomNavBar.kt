package com.shaphr.accessanotes.ui.components

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.shaphr.accessanotes.Destination

//Bottom Navigation Bar
@Composable
fun BottomNavBar(navController: NavController) {
    val curScreen = navController.currentBackStackEntry?.destination?.route

    val selectedItem = remember { mutableStateOf(curScreen) }


    NavigationBar {
        NavigationBarItem(
            icon = {Icon(Icons.Outlined.Home, contentDescription = "Home Icon")},
            label = {Text("All Notes")},
            selected = selectedItem.value == Destination.NoteRepositoryScreen.route,
            onClick = { navController.navigate(Destination.NoteRepositoryScreen.route) }
        )
        NavigationBarItem(
            icon = {Icon(Icons.Outlined.Add, contentDescription = "Add Icon")},
            label = {Text("New Session")},
            selected = selectedItem.value == Destination.SessionStartAndEndScreen.route,
            onClick = { navController.navigate(Destination.SessionStartAndEndScreen.route)}
        )
        NavigationBarItem(
            icon = {Icon(Icons.Outlined.AccountCircle, contentDescription = "Account Icon")},
            label = {Text("Account")},
            selected = selectedItem.value == Destination.AccountScreen.route,
            onClick = { navController.navigate(Destination.AccountScreen.route) }
        )
    }
}