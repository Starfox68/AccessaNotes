package com.shaphr.accessanotes

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAIAPIClient @Inject constructor() {
    private val modelName: String = "gpt-3.5-turbo"
    private val token = BuildConfig.OPENAI_API_KEY // Value needs to be set in local.properties
    private val openAIClient = OpenAI(token)

    @OptIn(BetaOpenAI::class)
    fun summarize(prompt: String, transcript: String): Flow<ChatCompletionChunk> {
        val request = ChatCompletionRequest(
            model = ModelId(modelName),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt + "\n\n" + transcript
                )
            )
        )

        return openAIClient.chatCompletions(request)
    }
}
