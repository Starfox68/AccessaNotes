package com.shaphr.accessanotes.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shaphr.accessanotes.data.repositories.AccountSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//View model for the account settings page
@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    private val accountSettingsRepository: AccountSettingsRepository
) : ViewModel() {

    //font size value
    val fontFlow: Flow<Boolean> = accountSettingsRepository.fontFlow

    //colourBlind mode value
    val colourBlindFlow: Flow<Boolean> = accountSettingsRepository.colourFlow

    //authenticated user boolean value
    private val mutableAuthenticated: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val authenticated: StateFlow<Boolean> = mutableAuthenticated

    //set authenticated user to true after authentication
    fun setAuthenticated(){
        mutableAuthenticated.value = true
    }

    //enable/disable large font size
    fun onFontClick(isLarge: Boolean){
        viewModelScope.launch {
            accountSettingsRepository.onFontChange(isLarge)
        }
    }

    //enable/disable colourBlind mode
    fun onColourClick(isEnabled: Boolean){
        viewModelScope.launch {
            accountSettingsRepository.onColourChange(isEnabled)
        }
    }
}