package com.shaphr.accessanotes.data.sources

import com.aallam.openai.api.BetaOpenAI
import com.shaphr.accessanotes.OpenAIAPIClient
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummarizedNoteSource @Inject constructor(
    private val openAIAPIClient: OpenAIAPIClient
) {
    val summarizedNotes: MutableSharedFlow<String> = MutableSharedFlow()

    @OptIn(BetaOpenAI::class)
    suspend fun summarize(prompt: String) {
        var summarizedNote = ""
        try {
            openAIAPIClient.summarize(prompt).collect { chunk ->
                summarizedNote += chunk.choices.first().delta?.content.orEmpty()
            }
        } finally {
            summarizedNotes.emit(summarizedNote)
        }
    }
}

