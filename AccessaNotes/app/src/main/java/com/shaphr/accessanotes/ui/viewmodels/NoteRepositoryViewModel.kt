package com.shaphr.accessanotes.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.shaphr.accessanotes.FileManagerAbstract
import com.shaphr.accessanotes.FileManagerDOCX
import com.shaphr.accessanotes.FileManagerPDF
import com.shaphr.accessanotes.FileManagerTXT
import com.shaphr.accessanotes.TextToSpeechClient
import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.data.models.UiNote
import com.shaphr.accessanotes.data.repositories.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteRepositoryViewModel @Inject constructor(
    application: Application,
    private val notesRepository: NotesRepository,
    private val textToSpeechClient: TextToSpeechClient
) : AndroidViewModel(application) {

    val notes: Flow<List<UiNote>> = notesRepository.notes

    var isSpeaking = false

    init {
        refreshNotes()
    }

    private fun refreshNotes() = viewModelScope.launch {
        notesRepository.refreshNotes()
    }

    fun setDocType(text: String) {
        mutableDocType.value = text
    }

    fun onTextToSpeech(text: String) {
        if (!isSpeaking) {
            textToSpeechClient.speak(text)
        } else {
            textToSpeechClient.stop()
        }

        isSpeaking = !isSpeaking
    }
    fun getNote(id: Int): Flow<UiNote?> {
        return notes.map { noteList ->
            noteList.firstOrNull { it.id == id }
        }
    }

    fun downloadNote(note: Note) {
        var fileManager: FileManagerAbstract? = null
        println("docType.value: ${docType.value}")
        when (docType.value) {
            "PDF" -> {
                // use FileManagerPDF
                fileManager = FileManagerPDF(getApplication())
            }
            "TXT" -> {
                // use FileManagerTXT
                fileManager = FileManagerTXT(getApplication())
            }
            "DOCX" -> {
                // use FileManagerDOCX
                fileManager = FileManagerDOCX(getApplication())
            }
        }
        println("downloadNote: ${note.title}")
        fileManager?.exportNote(note.title, listOf(note.summarizeContent))
    }

    fun updateNote(note: Note) {
        notesRepository.updateNote(note)
    }
}