package com.shaphr.accessanotes.data.repositories

import com.shaphr.accessanotes.TranscriptionClient
import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.data.sources.SummarizedNoteSource
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LiveRecordingRepository @Inject constructor(
    private val summarizedNoteSource: SummarizedNoteSource,
    private val transcriptionClient: TranscriptionClient
) {
    // Final summarized note text
    val summarizedNotesFlow: MutableSharedFlow<String> = summarizedNoteSource.summarizedNotes

    // Recorded text to summarize
    val transcriptFlow: MutableSharedFlow<String> = transcriptionClient.transcription

    private val summarizedNote: MutableList<String> = mutableListOf()

    private val transcript: MutableList<String> = mutableListOf()

    var title: String = ""

    var date: LocalDate = LocalDate.now()

    suspend fun summarizeRecording(prompt: String) {
        transcriptFlow.collect {
            transcript.add(it)
            summarizedNoteSource.summarize(prompt, it)
        }
    }

    suspend fun collectSummaries() {
        summarizedNotesFlow.collect {
            summarizedNote.add(it)
        }
    }

    fun startRecording() = transcriptionClient.startRecording()

    suspend fun stopRecording() {
        transcriptionClient.stopRecording()
    }

    fun onFinish(): Note {
        val note = Note(
            title = title,
            date = date,
            content = summarizedNote.reduce { acc: String, next: String ->
                acc + next
            }
        )
        summarizedNote.clear()
        transcript.clear()
        title = ""
        return note
    }
}