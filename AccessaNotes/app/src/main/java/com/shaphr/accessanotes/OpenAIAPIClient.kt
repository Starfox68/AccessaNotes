package com.shaphr.accessanotes

import com.aallam.openai.api.edits.EditsRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.runBlocking

class OpenAIAPIClient(private val modelName: String = "text-davinci-edit-001") {
    private val token = BuildConfig.OPENAI_API_KEY // Value needs to be set in local.properties
    private val openAIClient = OpenAI(token)
    private val instruction = "Summarize the given text into notes using bullet points"

    fun summarize(prompt: String): String = runBlocking {
        val edit = openAIClient.edit(
            request = EditsRequest(
                model = ModelId(modelName),
                input = prompt,
                instruction = instruction
            )
        )

        return@runBlocking edit.choices[0].text
    }
}