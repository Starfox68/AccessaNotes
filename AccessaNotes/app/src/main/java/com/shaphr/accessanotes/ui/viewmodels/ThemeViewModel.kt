package com.shaphr.accessanotes.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.shaphr.accessanotes.data.repositories.AccountSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

//View model for overall app theme storing font and colour settings
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val accountSettingsRepository: AccountSettingsRepository
) : ViewModel() {
    val fontFlow: Flow<Boolean> = accountSettingsRepository.fontFlow
    val colourFlow: Flow<Boolean> = accountSettingsRepository.colourFlow
}