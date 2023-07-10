package com.shaphr.accessanotes.ui.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.mutableStateOf

class StartAndEndScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val mutableTitle: MutableStateFlow<String> = MutableStateFlow("")
    val title: StateFlow<String> = mutableTitle

    private val mutablePrompt: MutableStateFlow<String> = MutableStateFlow("")
    val prompt: StateFlow<String> = mutablePrompt

    private val mutableStart: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val canStart: StateFlow<Boolean> = mutableStart

    private val mutableFileText: MutableStateFlow<String> = MutableStateFlow("")
    val fileText: StateFlow<String> = mutableFileText

    init { }

    fun readFile(uri: Uri) {
        val mimeType = getApplication<Application>().contentResolver.getType(uri)
        val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)

        when (mimeType) {
            "text/plain" -> {
                // read from .txt file
                val text = inputStream?.bufferedReader()?.use { it.readText() }
                if (text != null) {
                    mutableFileText.value = text
                }
            }
            // add cases for other file types here
        }
    }

    fun setPrompt(text: String) {
        mutablePrompt.value = text
    }

    fun setTitle(text: String) {
        mutableTitle.value = text
        mutableStart.value = title.value.isNotEmpty()
    }
}
