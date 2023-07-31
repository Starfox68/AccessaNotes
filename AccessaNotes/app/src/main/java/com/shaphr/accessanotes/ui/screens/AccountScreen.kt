package com.shaphr.accessanotes.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.common.api.ApiException
import com.shaphr.accessanotes.AuthResultContract
import com.shaphr.accessanotes.ui.components.TopScaffold
import com.shaphr.accessanotes.ui.viewmodels.AccountSettingsViewModel

//Wrapper for user account settings
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AccountScreen(
    navController: NavHostController,
) {
    val viewModel: AccountSettingsViewModel = hiltViewModel()

    //Pass in screen content to the scaffold
    //collect values and functions from the view model
    TopScaffold(text = "Account Settings", navController = navController) { padding ->
        AccountScreenContent(
            padding,
            onFontChange = viewModel::onFontClick,
            onColourChange = viewModel::onColourClick,
            isColourBlind = viewModel.colourBlindFlow.collectAsState(initial = false).value,
            isFontLarge = viewModel.fontFlow.collectAsState(initial = false).value,
            authenticateUser = viewModel::setAuthenticated,
            authConnected = viewModel.authenticated.collectAsState(initial = false).value
        )
    }
}

//User account settings screen
@Composable
fun AccountScreenContent(
    padding: PaddingValues,
    onFontChange: (Boolean) -> Unit,
    onColourChange: (Boolean) -> Unit,
    isColourBlind: Boolean,
    isFontLarge: Boolean,
    authenticateUser: () -> Unit,
    authConnected: Boolean
) {

    //google sign-in auth + google drive authenticator
    val context = LocalContext.current
    val signInRequestCode = 1

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) { task ->
            try {
                val account = task?.getResult(ApiException::class.java)
                if (account == null) {
                    Log.d("drive", "account is null")
                }else{
                    Log.d("HI", account.displayName!!)
                    authenticateUser()
                }
            } catch (e: ApiException) {
                Log.d("drive", "sign in failed")
            }
        }

    //Vertically Align buttons for changing user settings
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(padding)
    ) {
        //Custom component for text and switch together
        SettingSwitch(text = "Enable Large Fonts", isEnabled = isFontLarge, onClick = onFontChange)
        SettingSwitch(
            text = "Enable Colourblind Mode",
            isEnabled = isColourBlind,
            onClick = onColourChange
        )
        //Change text and button depending on if the user is signed in or not
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp, 0.dp, 10.dp, 0.dp)
        ) {
            if (authConnected){
                Text("Connected to Google Drive", style = MaterialTheme.typography.bodyMedium)
            }else{
                Text("Connect to Google Drive", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { authResultLauncher.launch(signInRequestCode) }, enabled = !authConnected) {
                Text("Connect", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

//Custom component for text and a slider switch beside it
@Composable
fun SettingSwitch(
    text: String,
    isEnabled: Boolean,
    onClick: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
            .clickable { onClick(!isEnabled) }
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.weight(1f))
        Switch(checked = isEnabled, onCheckedChange = onClick)
    }
}
