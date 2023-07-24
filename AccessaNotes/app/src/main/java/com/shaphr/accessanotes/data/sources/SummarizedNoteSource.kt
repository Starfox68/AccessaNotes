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
    val summarizedNotes: MutableSharedFlow<String> = MutableSharedFlow(replay = 0)

    @OptIn(BetaOpenAI::class)
    suspend fun summarize(prompt: String, transcript: String) {
        openAIAPIClient.summarize(prompt, transcript).collect { chunk ->
            summarizedNotes.emit(chunk.choices.first().delta?.content.orEmpty())
        }
    }
}

