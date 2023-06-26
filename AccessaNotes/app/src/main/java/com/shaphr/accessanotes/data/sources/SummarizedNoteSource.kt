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
        openAIAPIClient.summarize(prompt).collect { chunk ->
            summarizedNotes.emit(chunk.choices.first().delta?.content.orEmpty())
        }
    }
}

