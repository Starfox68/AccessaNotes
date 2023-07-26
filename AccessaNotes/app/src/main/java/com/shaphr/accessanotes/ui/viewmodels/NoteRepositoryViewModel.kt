package com.shaphr.accessanotes.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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

    private val mutableFileFormat = MutableStateFlow(FileFormat.PDF)
    val fileFormat = mutableFileFormat

    private val mutableDialogState = MutableStateFlow(DialogState.CLOSED)
    val dialogState: StateFlow<DialogState> = mutableDialogState

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

    private fun downloadNote(fileFormat: FileFormat) {
        val notes = getSelectedNotes()

        val fileManager = when (fileFormat) {
            FileFormat.PDF -> FileManagerPDF(getApplication())
            FileFormat.DOCX -> FileManagerDOCX(getApplication())
            FileFormat.TXT -> FileManagerTXT(getApplication())
        }
        notes.forEach { note ->
            println("downloadNote: ${note.title}")
            fileManager.exportNote(note.title, listOf(note.summarizeContent))
        }
    }

    fun getSelectedNotes(): List<Note> {
        return mutableNotes.value.filter {
            it.id in mutableSelectedNotes.value
        }
    }

    fun showDialog(dialogState: DialogState) {
        mutableDialogState.value = dialogState
    }

    fun onDialogClose() {
        mutableDialogState.value = DialogState.CLOSED
    }

    fun onDialogConfirm(fileFormat: FileFormat) {
        mutableFileFormat.value = fileFormat
        downloadNote(fileFormat)
    }

    fun onDeleteClick() {
        val notes = getSelectedNotes()
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

enum class FileFormat(val text: String) {
    PDF("PDF"),
    DOCX("DOCX"),
    TXT("TXT")
}

enum class DialogState {
    CLOSED,
    SHARE_OPEN,
    DOWNLOAD_OPEN
}
