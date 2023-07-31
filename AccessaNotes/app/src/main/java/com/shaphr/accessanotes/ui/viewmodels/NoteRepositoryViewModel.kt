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

/**
 * ViewModel used for managing notes. This class is responsible for fetching, deleting, downloading and handling UI interactions for notes.
 * @property notes StateFlow that emits current list of UiNotes from repository.
 * @property fileFormat MutableStateFlow that maintains the current file format for download/export.
 * @property dialogState MutableStateFlow that manages the current state of the dialog.
 * @property allSelected MutableStateFlow that determines if all notes are selected.
 * @property selectedNotes MutableStateFlow that maintains the list of currently selected note IDs.
 */
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

    /**
     * Fetches notes from the repository and updates the flow of notes.
     */
    private fun refreshNotes() = viewModelScope.launch {
        notesRepository.refreshNotes()
    }

    /**
     * Downloads selected notes in the given format.
     * @param fileFormat Format in which to download the notes.
     */
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

    /**
     * Fetches the selected notes based on the IDs from the flow of selected notes.
     * @return List of UiNote objects that are currently selected.
     */
    fun getSelectedNotes(): List<UiNote> {
        return notes.value.filter {
            it.id in mutableSelectedNotes.value
        }
    }

    /**
     * Sets the dialog state to a given state.
     * @param dialogState The state to which the dialog should be set.
     */
    fun showDialog(dialogState: DialogState) {
        mutableDialogState.value = dialogState
    }

    /**
     * Closes the dialog by setting the dialog state to CLOSED.
     */
    fun onDialogClose() {
        mutableDialogState.value = DialogState.CLOSED
    }

    /**
     * Confirms the dialog action and initiates download of notes in the specified format.
     * @param fileFormat Format in which to download the notes.
     */
    fun onDialogConfirm(fileFormat: FileFormat) {
        mutableFileFormat.value = fileFormat
        downloadNote(fileFormat)
    }

    /**
     * Deletes the selected notes and clears the selection.
     */
    fun onDeleteClick() {
        val notes = getSelectedNotes()
        notes.forEach {
            notesRepository.deleteNote(it)
        }
        mutableSelectedNotes.value = emptyList()
    }

    /**
     * Selects or deselects all notes.
     * @param isAllSelected Flag indicating whether all notes should be selected.
     */
    fun onAllSelect(isAllSelected: Boolean) {
        if (isAllSelected) {
            mutableSelectedNotes.value = notes.value.map { it.id }
        } else {
            mutableSelectedNotes.value = emptyList()
        }
        mutableAllSelected.value = isAllSelected
    }

    /**
     * Selects or deselects a specific note.
     * @param isSelected Flag indicating whether the note should be selected.
     * @param id ID of the note to select/deselect.
     */
    fun onNoteSelect(isSelected: Boolean, id: Int) {
        if (isSelected) {
            mutableSelectedNotes.value = mutableSelectedNotes.value + id
        } else {
            mutableSelectedNotes.value = mutableSelectedNotes.value - id
        }
        mutableAllSelected.value = mutableSelectedNotes.value.size == notes.value.size
    }
}

/**
 * Enum representing different file formats
 */
enum class FileFormat(val text: String) {
    PDF("PDF"),
    DOCX("DOCX"),
    TXT("TXT")
}

/**
 * Enum representing different dialog states
 */
enum class DialogState {
    CLOSED,
    SHARE_OPEN,
    DOWNLOAD_OPEN
}
