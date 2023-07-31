package com.shaphr.accessanotes.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shaphr.accessanotes.FileManagerDOCX
import com.shaphr.accessanotes.FileManagerPDF
import com.shaphr.accessanotes.FileManagerTXT
import com.shaphr.accessanotes.data.models.UiNote
import com.shaphr.accessanotes.data.repositories.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteRepositoryViewModel @Inject constructor(
    application: Application,
    private val notesRepository: NotesRepository,
) : AndroidViewModel(application) {

    val notes: StateFlow<List<UiNote>> = notesRepository.notes

    private val mutableFileFormat = MutableStateFlow(FileFormat.PDF)
    val fileFormat = mutableFileFormat

    private val mutableDialogState = MutableStateFlow(DialogState.CLOSED)
    val dialogState: StateFlow<DialogState> = mutableDialogState

    private val mutableAllSelected = MutableStateFlow(false)
    val allSelected: StateFlow<Boolean> = mutableAllSelected

    private val mutableSelectedNotes = MutableStateFlow<List<Int>>(emptyList())
    val selectedNotes: StateFlow<List<Int>> = mutableSelectedNotes
    init {}

    private fun refreshNotes() = viewModelScope.launch {
        notesRepository.refreshNotes()
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

    fun getSelectedNotes(): List<UiNote> {
        return notes.value.filter {
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
            mutableSelectedNotes.value = notes.value.map {
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
        mutableAllSelected.value = mutableSelectedNotes.value.size == notes.value.size
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
