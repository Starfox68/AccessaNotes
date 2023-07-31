package com.shaphr.accessanotes.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

//Top Navigation Bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNav(titleText: String) {
    TopAppBar(
        title = { Text(titleText) },
        actions = {
            IconButton(onClick = { }){
                Icon(imageVector = Icons.Default.Settings, null)
            }
        }
    )
}