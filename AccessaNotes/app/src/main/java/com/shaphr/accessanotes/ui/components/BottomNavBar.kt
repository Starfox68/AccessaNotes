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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shaphr.accessanotes.Destination

//return icon depending on the tab name
@Composable
fun StyleIcons(iconName: String){
    val mod = Modifier.defaultMinSize(40.dp, 40.dp)

    var shape = Icons.Outlined.Home
    if (iconName == "Add"){
        shape = Icons.Outlined.Add
    }else if (iconName == "AccountCircle"){
        shape = Icons.Outlined.AccountCircle
    }

    return Icon(shape, contentDescription = iconName, mod, Color(0xFF2E5399))
}

//Bottom Navigation Bar
@Composable
fun BottomNavBar(navController: NavController) {
    //show current screen as the selected item
    val curScreen = navController.currentBackStackEntry!!.destination.route!!
    val selectedItem = remember { mutableStateOf(curScreen) }

    //Nav bar with 3 items and custom route's
    NavigationBar(modifier = Modifier.defaultMinSize(50.dp, 90.dp)) {
        NavigationBarItem(
            icon = { StyleIcons("Home")},
            label = {Text("All Notes", color = Color(0xFF2E5399))},
            selected = selectedItem.value == Destination.NoteRepositoryScreen.route,
            onClick = { navController.navigate(Destination.NoteRepositoryScreen.route) }
        )
        NavigationBarItem(
            icon = { StyleIcons("Add")},
            label = {Text("New Session", color = Color(0xFF2E5399))},
            selected = selectedItem.value == Destination.SessionStartAndEndScreen.route,
            onClick = { navController.navigate(Destination.SessionStartAndEndScreen.route)}
        )
        NavigationBarItem(
            icon = { StyleIcons("AccountCircle")},
            label = {Text("Account", color = Color(0xFF2E5399))},
            selected = selectedItem.value == Destination.AccountScreen.route,
            onClick = { navController.navigate(Destination.AccountScreen.route) }
        )
    }
}