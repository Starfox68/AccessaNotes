package com.shaphr.accessanotes.data.repositories

import com.shaphr.accessanotes.data.sources.SummarizedNoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LiveRecordingRepository @Inject constructor(
    private val summarizedNoteSource: SummarizedNoteSource,
) {
    // Final summarized note text
    val summarizedNotes: MutableSharedFlow<String> = summarizedNoteSource.summarizedNotes

    // Recorded text to summarize
    // TODO: Hook this up to the API
    private val recording: Flow<String> = flow {
        emit("Prompt 1")
        emit("A ViewModelScope is defined for each ViewModel in your app. Any coroutine " +
                "launched in this scope is automatically canceled if the ViewModel is cleared.")
    }

    suspend fun summarizeRecording() {
        recording.collect {
            summarizedNoteSource.summarize(it)
        }
    }
}