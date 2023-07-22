package com.shaphr.accessanotes.ui.screens

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.shaphr.accessanotes.ui.components.BottomNavBar

//Account Screen
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navController: NavHostController,
//    viewModel: AuthViewModel = hiltViewModel()
) {
//    val AuthSettings = viewModel.settings.collectAsState().value

    Scaffold (
        content = { Text("HI")  },
        bottomBar = {
            BottomNavBar(navController)
        }
    )
}
