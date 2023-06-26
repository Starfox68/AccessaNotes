package com.shaphr.accessanotes.ui.viewmodels

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
    private val liveRecordingRepository: LiveRecordingRepository,
) : ViewModel() {

    private val mutableNoteText: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val noteText: StateFlow<List<String>> = mutableNoteText

    init {
        viewModelScope.launch {
            liveRecordingRepository.summarizeRecording()
            liveRecordingRepository.summarizedNotes.collect { summarizedNote ->
                mutableNoteText.update {
                    it + listOf(summarizedNote)
                }
            }
        }
    }

    fun onStartRecording() {
        // TODO Start Audio Capture
    }

    fun onStopRecording() {
        // TODO Stop Audio Capture
    }
}