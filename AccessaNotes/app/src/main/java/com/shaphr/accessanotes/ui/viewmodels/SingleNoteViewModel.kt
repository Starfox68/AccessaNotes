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
    val mutableNotes: MutableStateFlow<List<UiNote>> = MutableStateFlow(emptyList())
    var noteId: Int = 0
    var isSpeaking = false

    //get list of notes from the note repository
    init {
        mutableNotes.value = notesRepository.notes.value
    }

    //update the content of a note when the user changes the text on screen
    fun updateNote(noteID: Int, newContent: String) {
        //find correct note
        mutableNotes.value = mutableNotes.value.map { note ->
            if (note.id == noteID) {
                //update content
                note.copy(summarizeContent = newContent)
            } else {
                note
            }
        }
    }

    //update note in the note repository
    fun updateNoteDB(note: UiNote?) {
        if (note != null) {
            notesRepository.updateNote(note)
        }
    }

    //get note based on ID
    fun getNote(id: Int): Flow<UiNote?> {
        noteId = id
        return mutableNotes.map { noteList ->
            noteList.firstOrNull { it.id == id }
        }
    }

    //start text to speech client
    fun onTextToSpeech(text: String) {
        if (!isSpeaking) {
            textToSpeechClient.speak(text)
        } else {
            textToSpeechClient.stop()
        }

        isSpeaking = !isSpeaking
    }
}