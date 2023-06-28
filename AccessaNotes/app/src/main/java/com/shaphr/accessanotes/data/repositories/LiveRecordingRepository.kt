package com.shaphr.accessanotes.data.repositories

import com.shaphr.accessanotes.TranscriptionClient
import com.shaphr.accessanotes.data.sources.SummarizedNoteSource
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LiveRecordingRepository @Inject constructor(
    private val summarizedNoteSource: SummarizedNoteSource,
    private val transcriptionClient: TranscriptionClient
) {
    // Final summarized note text
    val summarizedNotes: MutableSharedFlow<String> = summarizedNoteSource.summarizedNotes

    // Recorded text to summarize
    val recording: MutableSharedFlow<String> = transcriptionClient.transcription

    suspend fun summarizeRecording(prompt: String) {
        recording.collect {
            summarizedNoteSource.summarize(prompt, it)
        }
    }

    fun startRecording() = transcriptionClient.startRecording()

    suspend fun stopRecording() {
        transcriptionClient.stopRecording()
    }
}