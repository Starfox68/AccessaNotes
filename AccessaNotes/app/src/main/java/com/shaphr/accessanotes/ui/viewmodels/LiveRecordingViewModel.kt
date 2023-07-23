package com.shaphr.accessanotes.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shaphr.accessanotes.TextToSpeechClient
import com.shaphr.accessanotes.data.repositories.LiveRecordingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LiveRecordingViewModel @Inject constructor(
    private val liveRecordingRepository: LiveRecordingRepository,
    private val textToSpeechClient: TextToSpeechClient
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

    // only initialized as an emergency default in case of failure, is  overwritten each time
    private var prompt: String = "Summarize the text"

    // Track if tts currently speaking
    var isSpeaking = false

    init {
        viewModelScope.launch {
            liveRecordingRepository.summarizedNotes.collect { summarizedNote ->
                Log.d("VIEW MODEL", summarizedNote)
                mutableNoteText.update {
                    it + listOf(summarizedNote)
                }
            }
        }

        viewModelScope.launch {
            liveRecordingRepository.recording.collect { transcribedText ->
                Log.d("VIEW MODEL", "Transcribed text: $transcribedText")
                mutableTranscribedText.update {
                    it + listOf(transcribedText)
                }
            }
        }

        viewModelScope.launch {
            resetTranscribedText()
            resetNoteText()
            delay(1500)
            mutableStop.update {
                true
            }
            liveRecordingRepository.startRecording()
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
