package com.shaphr.accessanotes.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AccountSettingsViewModel @Inject constructor(
//    private val AccountSettingsRepository,
) : ViewModel() {

    //read original size from repository when it's set up
    private val mutableFontSize: MutableStateFlow<Int> = MutableStateFlow(12)
    val fontSize: StateFlow<Int> = mutableFontSize

    private val mutableColBlindMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val colBlindMode: StateFlow<Boolean> = mutableColBlindMode

    fun increaseFont(){
        //change this
        if (mutableFontSize.value <= 23){
            mutableFontSize.value = mutableFontSize.value + 1
        }
    }

    fun decreaseFont(){
        //change this
        if (mutableFontSize.value >= 13){
            mutableFontSize.value = mutableFontSize.value - 1
        }
    }

    fun toggleColBlindMode(){
        mutableColBlindMode.value = !mutableColBlindMode.value
    }





}