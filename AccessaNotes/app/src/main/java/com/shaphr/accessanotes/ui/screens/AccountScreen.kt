package com.shaphr.accessanotes.ui.screens

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.shaphr.accessanotes.ui.components.TopScaffold
import com.shaphr.accessanotes.ui.viewmodels.AccountSettingsViewModel

@Composable
fun AccountScreen(
    navController: NavHostController,
) {
    val viewModel: AccountSettingsViewModel = hiltViewModel()

    TopScaffold(text = "Account Settings", navController = navController) { padding ->
        AccountScreenContent(
            padding,
            onFontChange = viewModel::onFontClick,
            onColourChange = viewModel::onColourClick,
            isColourBlind = viewModel.colourBlindFlow.collectAsState(initial = false).value,
            isFontLarge = viewModel.fontFlow.collectAsState(initial = false).value
        )
    }
}

@Composable
fun AccountScreenContent(
    padding: PaddingValues,
    onFontChange: (Boolean) -> Unit,
    onColourChange: (Boolean) -> Unit,
    isColourBlind: Boolean,
    isFontLarge: Boolean
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(padding)
    ) {
        SettingSwitch(text = "Enable Large Fonts", isEnabled = isFontLarge, onClick = onFontChange)
        SettingSwitch(
            text = "Enable Colourblind Mode",
            isEnabled = isColourBlind,
            onClick = onColourChange
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp, 0.dp, 10.dp, 0.dp)
        ) {
            Text("Connect to Google Drive", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { /*TODO*/ }) {
                Text("Connect", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

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

@Preview
@Composable
fun Preview() {
    AccountScreenContent(
        padding = PaddingValues(8.dp),
        onFontChange = { },
        onColourChange = { },
        isColourBlind = false,
        isFontLarge = true
    )
}
