package com.shaphr.accessanotes.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.shaphr.accessanotes.Destination
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

    // A prompt variable, initialized with a default value
    // This will be used to summarize the recorded text
    // This value can be updated later using the `updatePrompt` function
    private var prompt: String = "Summarize the text"

    // Initialize the ViewModel and set up the necessary listeners and actions
    init {
        viewModelScope.launch {
            // Collect transcribed text from the repository and update the mutableTranscribedText
            merge(liveRecordingRepository.transcriptFlow,
                liveRecordingRepository.bareTranscriptFlow).collect { transcribedText ->
                Log.d("VIEW MODEL", "Transcribed text: $transcribedText")
                mutableTranscribedText.update {
                    it + listOf(transcribedText)
                }
            }
        }

        viewModelScope.launch {
            // Collect summarized notes from the repository and update the mutableNoteText
            liveRecordingRepository.summarizedNotesFlow.collect { summarizedNote ->
                Log.d("VIEW MODEL", summarizedNote)
                mutableNoteText.update {
                    it + listOf(summarizedNote)
                }
            }
        }

        viewModelScope.launch {
            // Reset transcribed text and note text, then delay to prevent overlap with recording started audio
            resetTranscribedText()
            resetNoteText()
            delay(1800)
            // Update the mutableStop to indicate that recording can be stopped
            mutableStop.update {
                true
            }
            println("audioExists")
            println(audioExists)
            println(audioExists.value)
            // If audio exists, call the "whisper" function with the audio URI
            // Otherwise, start recording
            if (audioExists.value != Uri.EMPTY) {
                println("audioExists.value.isNotEmpty()")
                liveRecordingRepository.callWhisper(audioExists.value)
            } else {
                liveRecordingRepository.startRecording()
            }
        }
    }

    // Function to update the URI of the existing audio
    fun setAudioExists(uri: Uri?) {
        if (uri != null) {
            mutableAudioExists.update {
                uri
            }
        }
    }

    // Function to reset the transcribed text
    fun resetTranscribedText() {
        mutableTranscribedText.update {
            emptyList()
        }
    }

    // Function to reset the note text
    fun resetNoteText() {
        mutableNoteText.update {
            emptyList()
        }
    }

    // Function to update the prompt used for summarizing the text
    fun updatePrompt(prompt: String) {
        if (prompt.isNotEmpty()) {
            this.prompt = prompt
        }
    }

    // Function to stop the recording
    fun stopRecording() {
        // Update the mutableStop to indicate that recording should stop
        mutableStop.update {
            false
        }
        // Update the mutableListen to indicate that audio can be listened to
        mutableListen.update {
            true
        }
        viewModelScope.launch {
            // Stop the recording through the repository
            liveRecordingRepository.stopRecording()
        }
        viewModelScope.launch {
            Log.d("VIEW MODEL", "Summarizing recording...")
            Log.d("VIEW MODEL", "The prompt used was: $prompt")
            // Summarize the recording using the provided prompt
            liveRecordingRepository.summarizeRecording(prompt)
        }
        viewModelScope.launch {
            // Collect the summaries from the repository
            liveRecordingRepository.collectSummaries()
        }
        viewModelScope.launch {
            // Collect the bare transcripts from the repository
            liveRecordingRepository.collectBareTranscript()
        }
    }

    // Function to be called when the ViewModel is closed or reset
    fun onClose() {
        // Clear the note text and transcribed text lists
        mutableNoteText.value = emptyList()
        mutableTranscribedText.value = emptyList()
    }

    // Function to be called when saving the recording
    fun onSave(navHostController: NavHostController) {
        val note = liveRecordingRepository.onFinish()
        notesRepository.setNote(note)
        // Clear the note text and transcribed text lists
        onClose()
        Log.d("TEST",  note.title + ", " + note.summarizeContent + ", " + note.date.toString() + ", " + note.id)
        // Pop the back stack and navigate to the NoteRepositoryScreen
        navHostController.popBackStack()
        navHostController.navigate(Destination.NoteRepositoryScreen.route)
    }

    // Function to start text-to-speech with the provided text
    fun startTextToSpeech(text: String) {
        textToSpeechClient.speak(text)
    }

    // Function to stop text-to-speech
    fun stopTextToSpeech() {
        textToSpeechClient.stop()
    }
}
