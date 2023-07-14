package com.shaphr.accessanotes.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.data.repositories.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NoteRepositoryViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val mutableNotes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = mutableNotes

    init {
        notesRepository.getNotes().observeForever { notes ->
            mutableNotes.value = notes
        }
    }
    fun getNote(id: Int) = notes.value.firstOrNull { it.id == id }
}