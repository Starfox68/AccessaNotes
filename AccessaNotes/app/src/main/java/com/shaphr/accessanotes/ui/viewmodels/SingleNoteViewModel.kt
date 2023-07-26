package com.shaphr.accessanotes.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.shaphr.accessanotes.TextToSpeechClient
import com.shaphr.accessanotes.data.models.UiNote
import com.shaphr.accessanotes.data.repositories.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SingleNoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val textToSpeechClient: TextToSpeechClient
) : ViewModel() {
    val mutableNote: MutableStateFlow<List<UiNote>> = MutableStateFlow(emptyList())
    var noteId: Int = 0
    var isSpeaking = false

    init {
        mutableNote.value = notesRepository.notes.value
    }

    fun onClose() {
        mutableNote.value.firstOrNull { it.id == noteId }?.let {
            notesRepository.updateNote(it)
        }
    }

    fun getNote(id: Int): Flow<UiNote?> {
        noteId = id
        return mutableNote.map { noteList ->
            noteList.firstOrNull { it.id == id }
        }
    }

    fun onTextToSpeech(text: String) {
        if (!isSpeaking) {
            textToSpeechClient.speak(text)
        } else {
            textToSpeechClient.stop()
        }

        isSpeaking = !isSpeaking
    }
}