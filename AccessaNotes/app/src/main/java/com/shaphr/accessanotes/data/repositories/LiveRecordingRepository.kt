package com.shaphr.accessanotes.data.repositories

import com.shaphr.accessanotes.TranscriptionClient
import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.data.models.UiNote
import com.shaphr.accessanotes.data.models.UiNoteItem
import com.shaphr.accessanotes.data.sources.SummarizedNoteSource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.util.Date
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

    // Bare transcription text
    val bareTranscriptFlow: MutableSharedFlow<String> = MutableStateFlow("")

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

    suspend fun collectBareTranscript() {
        bareTranscriptFlow.collect {
            transcript.add(it)
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

    fun onFinish(): UiNote {
        val note = UiNote(
            title = title,
            date = date,
            summarizeContent = if (summarizedNote.isEmpty()) "" else summarizedNote.reduce { acc: String, next: String ->
                acc + next
            },
            transcript = if (transcript.isEmpty()) "" else transcript.reduce { acc: String, next: String ->
                acc + next
            },
            date = date,
        )
        summarizedNote.clear()
        transcript.clear()
        title = ""
        return note
    }
}