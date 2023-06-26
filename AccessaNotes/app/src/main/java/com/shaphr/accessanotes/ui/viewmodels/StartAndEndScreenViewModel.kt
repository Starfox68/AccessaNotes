package com.shaphr.accessanotes.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StartAndEndScreenViewModel : ViewModel() {

    private val mutableTitle: MutableStateFlow<String> = MutableStateFlow("")
    val title: StateFlow<String> = mutableTitle

    private val mutablePrompt: MutableStateFlow<String> = MutableStateFlow("")
    val prompt: StateFlow<String> = mutablePrompt

    private val mutableStart: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val canStart: StateFlow<Boolean> = mutableStart

    init { }

    fun setTitle(text: String) {
        mutableTitle.value = text
        mutableStart.value = title.value.isNotEmpty()
    }
}