package com.shaphr.accessanotes.ui.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shaphr.accessanotes.FileManagerAbstract
import com.shaphr.accessanotes.FileManagerDOCX
import com.shaphr.accessanotes.FileManagerPDF
import com.shaphr.accessanotes.FileManagerTXT
import com.shaphr.accessanotes.data.repositories.LiveRecordingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StartAndEndScreenViewModel @Inject constructor(
    application: Application,
    private val liveRecordingRepository: LiveRecordingRepository
) : AndroidViewModel(application) {

    //recording title
    private val mutableTitle: MutableStateFlow<String> = MutableStateFlow("")
    val title: StateFlow<String> = mutableTitle

    //recording prompt
    private val mutablePrompt: MutableStateFlow<String> = MutableStateFlow("")
    val prompt: StateFlow<String> = mutablePrompt

    //recording audioFile selected
    private val mutableAudioFile: MutableStateFlow<Uri> = MutableStateFlow(Uri.EMPTY)
    val audioFile: StateFlow<Uri> = mutableAudioFile

    //whether the user has input enough information to start a recording
    private val mutableStart: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val canStart: StateFlow<Boolean> = mutableStart

    //file selected by user to use for recording
    private val mutableFileText: MutableStateFlow<String> = MutableStateFlow("")
    val fileText: StateFlow<String> = mutableFileText

    //initialize live recording variables since the user will navigate here after clicking start
    init {
        liveRecordingRepository.date = LocalDate.now()
        viewModelScope.launch {
            mutableTitle.collect {
                liveRecordingRepository.title = it
            }
        }
    }

    //get file content for different file types
    suspend fun getFileContext(uri: Uri) {
        val mimeType = getApplication<Application>().contentResolver.getType(uri)
        var fileManager: FileManagerAbstract? = null

        when (mimeType) {
            "text/plain" -> {  // read from .txt file
                fileManager = FileManagerTXT(getApplication())
            }
            "application/pdf" -> {  // read from .pdf file
                fileManager = FileManagerPDF(getApplication())
            }
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> {  // read from .docx file
                fileManager = FileManagerDOCX(getApplication())
            } else -> {
                println("Error: Invalid file type")
            }
        }

        //get file based on uri
        if (fileManager != null) {
            mutableFileText.value = fileManager.importFile(uri)
        }
    }

    //set prompt after user types
    fun setPrompt(text: String) {
        mutablePrompt.value = text
    }

    //set audio file after a user selects it
    fun setAudioFile(uri: Uri) {
        mutableAudioFile.value = uri
    }

    //set title after a user types, and make start button clickable if title has text
    fun setTitle(text: String) {
        mutableTitle.value = text
        mutableStart.value = title.value.isNotEmpty()
    }
}
