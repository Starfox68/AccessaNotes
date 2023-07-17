package com.shaphr.accessanotes.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.shaphr.accessanotes.TextToSpeechClient
import com.shaphr.accessanotes.data.models.Note
import com.shaphr.accessanotes.data.repositories.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NoteRepositoryViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val textToSpeechClient: TextToSpeechClient
) : ViewModel() {

    private val mutableNotes: MutableStateFlow<List<Note>> = MutableStateFlow(notesRepository.getNotes())
    val notes: StateFlow<List<Note>> = mutableNotes
    // Track if tts currently speaking
    var isSpeaking = false

    fun getNote(id: Int) = notesRepository.getNotes().first { it.id == id }

    fun onTextToSpeech(text: String) {
        if (!isSpeaking) {
            textToSpeechClient.speak(text)
        } else {
            textToSpeechClient.stop()
        }

        isSpeaking = !isSpeaking
    }
}
