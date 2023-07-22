package com.shaphr.accessanotes.ui.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shaphr.accessanotes.Destination

@Composable
fun StyleIcons(icon: String, curScreen: String){
    val mod = Modifier
        .defaultMinSize(40.dp, 40.dp)

    return when(icon){
        "Home" -> Icon(Icons.Outlined.Home, contentDescription = icon, mod)
        "Add" -> Icon(Icons.Outlined.Add, contentDescription = icon, mod)
        "AccountCircle" -> Icon(Icons.Outlined.AccountCircle, contentDescription = icon, mod)
        else -> return
    }
}


//Bottom Navigation Bar
@Composable
fun BottomNavBar(navController: NavController) {
    val curScreen = navController.currentBackStackEntry!!.destination.route!!

    val selectedItem = remember { mutableStateOf(curScreen) }

    NavigationBar(modifier = Modifier.defaultMinSize(50.dp, 90.dp)) {
        NavigationBarItem(
            icon = { StyleIcons("Home", curScreen)},
            label = {Text("All Notes")},
            selected = selectedItem.value == Destination.NoteRepositoryScreen.route,
            onClick = { navController.navigate(Destination.NoteRepositoryScreen.route) }
        )
        NavigationBarItem(
            icon = { StyleIcons("Add", curScreen)},
            label = {Text("New Session")},
            selected = selectedItem.value == Destination.SessionStartAndEndScreen.route,
            onClick = { navController.navigate(Destination.SessionStartAndEndScreen.route)}
        )
        NavigationBarItem(
            icon = { StyleIcons("AccountCircle", curScreen)},
            label = {Text("Account")},
            selected = selectedItem.value == Destination.AccountScreen.route,
            onClick = { navController.navigate(Destination.AccountScreen.route) }
        )
    }
}