package com.shaphr.accessanotes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//Top Navigation Bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNav(titleText: String) {
    TopAppBar(
        title = { Text(titleText) },
        actions = {
            IconButton(onClick = { /* TODO */ }){
                Icon(imageVector = Icons.Default.Settings, null)
            }
        }
    )
}