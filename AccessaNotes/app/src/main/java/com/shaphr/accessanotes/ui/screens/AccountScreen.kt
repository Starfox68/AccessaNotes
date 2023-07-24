package com.shaphr.accessanotes.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.ui.components.BottomNavBar
import com.shaphr.accessanotes.ui.viewmodels.AccountSettingsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navController: NavHostController,
) {
    val viewModel: AccountSettingsViewModel = hiltViewModel()

    Scaffold (
        topBar = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 60.dp, 0.dp, 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center ) {
                Text(text="Account Settings", fontSize = 40.sp, maxLines = 1)
            }
        },
        content = { padding ->
            AccountScreenContent(padding)
        },
        bottomBar = {
            BottomNavBar(navController)
        }
    )
}

@Composable
fun AccountScreenContent(
    padding: PaddingValues,
    onFontChange: (Boolean) -> Unit,
    onColourChange: (Boolean) -> Unit,
    isColourBlind: Boolean,
    isFontLarge: Boolean
) {
    LazyColumn(modifier = Modifier.padding(padding)) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)
            ) {
                Text("Change Font Size", color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp),
                    onClick = { onColourChange(!isColourBlind) }) {
                    Text("-")
                }
                Button(
                    modifier = Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp),
                    onClick = { onFontChange(!isFontLarge) }) {
                    Text("+")
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(20.dp, 0.dp, 20.dp, 0.dp)
            ) {
                Text("Turn on colour blind mode", color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = checkedState.value,
                    onCheckedChange = { checkedState.value = it; viewModel.toggleColBlindMode() })
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(20.dp, 0.dp, 20.dp, 0.dp)
            ) {
                Text("Connect to Google Drive", color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp),
                    onClick = { /*TODO*/ }) {
                    Text("Connect")
                }
            }
        }
    }
}
