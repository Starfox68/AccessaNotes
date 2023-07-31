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

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    private val accountSettingsRepository: AccountSettingsRepository
) : ViewModel() {

    val fontFlow: Flow<Boolean> = accountSettingsRepository.fontFlow

    val colourBlindFlow: Flow<Boolean> = accountSettingsRepository.colourFlow

    private val mutableAuthenticated: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val authenticated: StateFlow<Boolean> = mutableAuthenticated

    fun setAuthenticated(){
        mutableAuthenticated.value = true
    }

    fun onFontClick(isLarge: Boolean){
        viewModelScope.launch {
            accountSettingsRepository.onFontChange(isLarge)
        }
    }

    fun onColourClick(isEnabled: Boolean){
        viewModelScope.launch {
            accountSettingsRepository.onColourChange(isEnabled)
        }
    }
}