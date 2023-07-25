package com.shaphr.accessanotes.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val notesRepository: NotesRepository,
    private val textToSpeechClient: TextToSpeechClient
) : ViewModel() {

    val notes: Flow<List<UiNote>> = notesRepository.notes

    var isSpeaking = false

    init {
        refreshNotes()
    }

    private fun refreshNotes() = viewModelScope.launch {
        notesRepository.refreshNotes()
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

}