package com.shaphr.accessanotes.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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
    application: Application,
    private val notesRepository: NotesRepository,
    private val textToSpeechClient: TextToSpeechClient
) : AndroidViewModel(application) {

    private val mutableNotes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = mutableNotes

    private val mutableIsDialogOpen = MutableStateFlow(false)
    val isDialogOpen: StateFlow<Boolean> = mutableIsDialogOpen

    private val mutableAllSelected = MutableStateFlow(false)
    val allSelected: StateFlow<Boolean> = mutableAllSelected

    private val mutableSelectedNotes = MutableStateFlow<List<Int>>(emptyList())
    val selectedNotes: StateFlow<List<Int>> = mutableSelectedNotes

    // Track if tts currently speaking
    var isSpeaking = false
    init {
        notesRepository.getNotes().observeForever { notes ->
            mutableNotes.value = notes
        }
    }

    private fun downloadNote(fileOptions: FileOptions) {
        val notes = getSelectedNotes(mutableSelectedNotes.value)

        val fileManager = when (fileOptions) {
            FileOptions.PDF -> FileManagerPDF(getApplication())
            FileOptions.DOCX -> FileManagerTXT(getApplication())
            FileOptions.TXT -> FileManagerDOCX(getApplication())
        }
        notes.forEach { note ->
            println("downloadNote: ${note.title}")
            fileManager.exportNote(note.title, listOf(note.summarizeContent))
        }
    }

    private fun getSelectedNotes(ids: List<Int>): List<Note> {
        return mutableNotes.value.filter {
            it.id in ids
        }
    }

    fun onDialogClose() {
        mutableIsDialogOpen.value = false
    }

    fun onDialogConfirm(fileOptions: FileOptions) {
        onDialogClose()
        downloadNote(fileOptions)
    }

    fun onDownloadClick() {
        mutableIsDialogOpen.value = true
    }

    fun onDeleteClick() {
        val notes = getSelectedNotes(mutableSelectedNotes.value)
        notes.forEach {
            notesRepository.deleteNote(it)
        }
        mutableSelectedNotes.value = emptyList()
    }

    fun onAllSelect(isAllSelected: Boolean) {
        if (isAllSelected) {
            mutableSelectedNotes.value = mutableNotes.value.map {
                it.id
            }
        } else {
            mutableSelectedNotes.value = emptyList()
        }
        mutableAllSelected.value = isAllSelected
    }

    fun onNoteSelect(isSelected: Boolean, id: Int) {
        if (isSelected) {
            mutableSelectedNotes.value = mutableSelectedNotes.value + id
        } else {
            mutableSelectedNotes.value = mutableSelectedNotes.value - id
        }
        mutableAllSelected.value = mutableSelectedNotes.value.size == mutableNotes.value.size
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

    fun updateNote(note: Note) {
        notesRepository.updateNote(note)
    }
}

enum class FileOptions(val text: String) {
    PDF("PDF"),
    DOCX("DOCX"),
    TXT("TXT")
}
