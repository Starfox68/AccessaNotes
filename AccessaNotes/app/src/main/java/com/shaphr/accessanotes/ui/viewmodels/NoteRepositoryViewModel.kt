package com.shaphr.accessanotes.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.shaphr.accessanotes.FileManagerAbstract
import com.shaphr.accessanotes.FileManagerDOCX
import com.shaphr.accessanotes.FileManagerPDF
import com.shaphr.accessanotes.FileManagerTXT
import com.shaphr.accessanotes.TextToSpeechClient
import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.data.repositories.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class NoteRepositoryViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val textToSpeechClient: TextToSpeechClient
) : ViewModel() {

    private val mutableNotes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = mutableNotes

    private val mutableDocType: MutableStateFlow<String> = MutableStateFlow("PDF")
    val docType: StateFlow<String> = mutableDocType

    // Track if tts currently speaking
    var isSpeaking = false
    init {
        notesRepository.getNotes().observeForever { notes ->
            mutableNotes.value = notes
        }
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

    fun getNote(id: Int) = notes.map { notes ->
        notes.firstOrNull {
            it.id == id
        }
    }

    fun downloadNote(note: Note) {
        var fileManager: FileManagerAbstract? = null
        when (docType.value) {
            "PDF" -> {
                // use FileManagerPDF
                fileManager = FileManagerPDF()
            }
            "TXT" -> {
                // use FileManagerTXT
                fileManager = FileManagerTXT()
            }
            "DOCX" -> {
                // use FileManagerDOCX
                fileManager = FileManagerDOCX()
            }
        }
        fileManager?.exportNote(note.title, listOf(note.summarizeContent))
    }
}