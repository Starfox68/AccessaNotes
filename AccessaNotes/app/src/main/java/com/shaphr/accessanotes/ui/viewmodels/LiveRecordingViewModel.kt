package com.shaphr.accessanotes.ui.viewmodels

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shaphr.accessanotes.data.repositories.LiveRecordingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LiveRecordingViewModel @Inject constructor(
    private val liveRecordingRepository: LiveRecordingRepository
) : ViewModel() {

    private val mutableNoteText: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val noteText: StateFlow<List<String>> = mutableNoteText

    private val mutableTranscribedText: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList())
    val transcribedText: StateFlow<List<String>> = mutableTranscribedText

    // only initialized as an emergency default in case of failure, is  overwritten each time
    private var prompt: String = "Summarize the text"

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
            liveRecordingRepository.startRecording()
        }
    }

    fun updatePrompt(prompt: String) {
        if (prompt.isNotEmpty()) {
            this.prompt = prompt
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            liveRecordingRepository.stopRecording()
        }
        viewModelScope.launch {
            Log.d("VIEW MODEL", "Summarizing recording...")
            Log.d("VIEW MODEL", "The prompt used was $prompt")
            liveRecordingRepository.summarizeRecording(prompt)
        }
    }

    fun onTextToSpeech(text: String, context: Context) {
        // TODO: Clean up and likely move elsewhere
        var status = -1
        val tts = TextToSpeech(context) {
            status = it
        }
        Handler(Looper.getMainLooper()).postDelayed({
                if (status == TextToSpeech.SUCCESS) {
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
                } else {
                    println("TTS failed, status: $status")
                }
            },
            1000
        )
    }
}
