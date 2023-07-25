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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StartAndEndScreenViewModel @Inject constructor(
    application: Application,
    private val liveRecordingRepository: LiveRecordingRepository
) : AndroidViewModel(application) {

    private val mutableTitle: MutableStateFlow<String> = MutableStateFlow("")
    val title: StateFlow<String> = mutableTitle

    private val mutablePrompt: MutableStateFlow<String> = MutableStateFlow("")
    val prompt: StateFlow<String> = mutablePrompt

    private val mutableStart: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val canStart: StateFlow<Boolean> = mutableStart

    private val mutableFileText: MutableStateFlow<String> = MutableStateFlow("")
    val fileText: StateFlow<String> = mutableFileText

    init {
        liveRecordingRepository.date = LocalDate.now()
        viewModelScope.launch {
            mutableTitle.collect {
                liveRecordingRepository.title = it
            }
        }
    }

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

        if (fileManager != null) {
            mutableFileText.value = fileManager.importFile(uri)
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
