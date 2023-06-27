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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class LiveRecordingViewModel @Inject constructor(
    private val liveRecordingRepository: LiveRecordingRepository,
) : ViewModel() {

    private val mutableNoteText: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val noteText: StateFlow<List<String>> = mutableNoteText

    private val mutableTranscribedText: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList())
    val transcribedText: StateFlow<List<String>> = mutableTranscribedText

    init {
        viewModelScope.launch {
            liveRecordingRepository.summarizeRecording()
        }

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
                Log.d("VIEW MODEL", transcribedText)
                mutableTranscribedText.update {
                    it + listOf(transcribedText)
                }
            }
        }

        viewModelScope.launch {
            liveRecordingRepository.startRecording()
            delay(30000)
            liveRecordingRepository.stopRecording()
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
                    tts.language = Locale.ENGLISH
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
                }
            },
            1000
        )
    }
}
