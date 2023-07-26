package com.shaphr.accessanotes.data.repositories

import android.graphics.Bitmap
import com.shaphr.accessanotes.TranscriptionClient
import com.shaphr.accessanotes.data.models.UiNote
import com.shaphr.accessanotes.data.models.UiNoteItem
import com.shaphr.accessanotes.data.sources.SummarizedNoteSource
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LiveRecordingRepository @Inject constructor(
    private val summarizedNoteSource: SummarizedNoteSource,
    private val transcriptionClient: TranscriptionClient,
) {
    // Final summarized note text
    val summarizedNotesFlow: MutableSharedFlow<String> = summarizedNoteSource.summarizedNotes

    // Recorded text to summarize
    val transcriptFlow: MutableSharedFlow<String> = transcriptionClient.transcription

    // Bare transcription text
    val bareTranscriptFlow: MutableSharedFlow<String> = MutableSharedFlow()

    val imageFlow: MutableSharedFlow<Bitmap> = MutableSharedFlow()

    val summary: MutableList<String> = mutableListOf()
    val summaryAndImages: MutableList<Any> = mutableListOf()

    val transcript: MutableList<String> = mutableListOf()

    var title: String = ""

    var date: LocalDate = LocalDate.now()

    suspend fun summarizeRecording(prompt: String) {
        transcriptFlow.collect {
            transcript.add(it)
            summarizedNoteSource.summarize(prompt, it)
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
            summarizeContent = if (summary.isEmpty()) "" else summary.reduce { acc: String, next: String ->
                acc + next
            },
            transcript = if (transcript.isEmpty()) "" else transcript.reduce { acc: String, next: String ->
                acc + next
            },
            items = summaryAndImages.mapIndexed { index, summaryOrImage ->
                if (summaryOrImage is String) {
                    UiNoteItem(
                        id = 0,
                        noteId = 0,
                        imageTrue = false,
                        content = summaryOrImage,
                        bitmap = null,
                        order = index
                    )
                } else {
                    UiNoteItem(
                        id = 0,
                        noteId = 0,
                        imageTrue = true,
                        content = null,
                        bitmap = summaryOrImage as Bitmap,
                        order = index
                    )
                }
            }
        )
        summaryAndImages.clear()
        transcript.clear()
        title = ""
        return note
    }
}