package com.shaphr.accessanotes

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

class OpenAIAPIClient(private val modelName: String = "gpt-3.5-turbo") {
    private val token = BuildConfig.OPENAI_API_KEY // Value needs to be set in local.properties
    private val openAIClient = OpenAI(token)
    private val instruction = "Summarize the given text into notes using nested bullet points:\n"

    @OptIn(BetaOpenAI::class)
    suspend fun summarize(prompt: String) = coroutineScope {
        val request = ChatCompletionRequest(
            model = ModelId(modelName),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = instruction + prompt
                )
            )
        )

        openAIClient.chatCompletions(request)
            .onEach { print(it.choices.first().delta?.content.orEmpty()) }
            .onCompletion { println() }
            .launchIn(this)
            .join()
    }
}
