package com.shaphr.accessanotes.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.shaphr.accessanotes.TextToSpeechClient
import com.shaphr.accessanotes.data.repositories.LiveRecordingRepository
import com.shaphr.accessanotes.data.repositories.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LiveRecordingViewModel @Inject constructor(
    private val liveRecordingRepository: LiveRecordingRepository,
    private val textToSpeechClient: TextToSpeechClient,
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val mutableNoteText: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val noteText: StateFlow<List<String>> = mutableNoteText

    private val mutableTranscribedText: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList())
    val transcribedText: StateFlow<List<String>> = mutableTranscribedText

    private val mutableStop: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val canStop: StateFlow<Boolean> = mutableStop

    private val mutableListen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val canListen: StateFlow<Boolean> = mutableListen

    private val mutableAudioExists: MutableStateFlow<Uri> = MutableStateFlow(Uri.EMPTY)
    val audioExists: StateFlow<Uri> = mutableAudioExists

    // only initialized as an emergency default in case of failure, is  overwritten each time
    private var prompt: String = "Summarize the text"

    init {
        viewModelScope.launch {
            merge(liveRecordingRepository.transcriptFlow,
                liveRecordingRepository.bareTranscriptFlow).collect { transcribedText ->
                Log.d("VIEW MODEL", "Transcribed text: $transcribedText")
                mutableTranscribedText.update {
                    it + listOf(transcribedText)
                }
            }
        }

        viewModelScope.launch {
            liveRecordingRepository.summarizedNotesFlow.collect { summarizedNote ->
                Log.d("VIEW MODEL", summarizedNote)
                mutableNoteText.update {
                    it + listOf(summarizedNote)
                }
            }
        }

        viewModelScope.launch {
            resetTranscribedText()
            resetNoteText()
            delay(1800) // Prevent overlap with recording started audio
            mutableStop.update {
                true
            }
            println("audioExists")
            println(audioExists)
            println(audioExists.value)
            if (audioExists.value != Uri.EMPTY) {
                println("audioExists.value.isNotEmpty()")
                liveRecordingRepository.callWhisper(audioExists.value)
            } else {
                liveRecordingRepository.startRecording()
            }
        }
    }

    fun setAudioExists(uri: Uri?) {
        if (uri != null) {
            mutableAudioExists.update {
                uri
            }
        }
    }

    fun resetTranscribedText() {
        mutableTranscribedText.update {
            emptyList()
        }
    }

    fun resetNoteText() {
        mutableNoteText.update {
            emptyList()
        }
    }

    fun updatePrompt(prompt: String) {
        if (prompt.isNotEmpty()) {
            this.prompt = prompt
        }
    }

    fun stopRecording() {
        mutableStop.update {
            false
        }
        mutableListen.update {
            true
        }
        viewModelScope.launch {
            liveRecordingRepository.stopRecording()
        }
        viewModelScope.launch {
            Log.d("VIEW MODEL", "Summarizing recording...")
            Log.d("VIEW MODEL", "The prompt used was: $prompt")
            liveRecordingRepository.summarizeRecording(prompt)
        }
        viewModelScope.launch {
            liveRecordingRepository.collectSummaries()
        }
        viewModelScope.launch {
            liveRecordingRepository.collectBareTranscript()
        }
    }

    fun onClose() {
        mutableNoteText.value = emptyList()
        mutableTranscribedText.value = emptyList()
    }

    fun onSave(navHostController: NavHostController) {
        val note = liveRecordingRepository.onFinish()
        notesRepository.setNote(note)
        onClose()
        Log.d("TEST",  note.title + ", " + note.summarizeContent + ", " + note.date.toString() + ", " + note.id)
        navHostController.popBackStack()
    }

    fun startTextToSpeech(text: String) {
        textToSpeechClient.speak(text)
    }

    fun stopTextToSpeech() {
        textToSpeechClient.stop()
    }
}
