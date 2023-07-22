package com.shaphr.accessanotes.ui.components

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
    val selectedItem = remember { mutableStateOf("All Notes") }

//    val t = navController.
    NavigationBar {
        NavigationBarItem(
            icon = {Icon(Icons.Outlined.Home, contentDescription = "All Notes")},
            label = {Text("All Notes")},
            selected = selectedItem.value == "All Notes",
            onClick = { selectedItem.value = "All Notes";
                navController.navigate(Destination.NoteRepositoryScreen.route) }
        )
        NavigationBarItem(
            icon = {Icon(Icons.Outlined.Add, contentDescription = "New Session")},
            label = {Text("New Session")},
            selected = selectedItem.value == "New Session",
            onClick = { selectedItem.value = "New Session";
                navController.navigate(Destination.SessionStartAndEndScreen.route)}
        )
        NavigationBarItem(
            icon = {Icon(Icons.Outlined.AccountCircle, contentDescription = "Account")},
            label = {Text("Account")},
            selected = selectedItem.value == "Account",
            onClick = { selectedItem.value = "Account";
                navController.navigate(Destination.AccountScreen.route) }
        )
    }
}