package com.shaphr.accessanotes

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import ru.gildor.coroutines.okhttp.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranscriptionClient @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val transcription: MutableSharedFlow<String> = MutableSharedFlow(replay = 0)

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private val filePath: String by lazy {
        context.getExternalFilesDir(null)?.absolutePath + "/transcript_recording.mp3"
    }

    fun startRecording() {
        println("Starting recording...")
        println("Audio File path: $filePath")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(filePath)
            prepare()
            start()
        }
    }

    suspend fun stopRecording() {
        println("Stopping recording...")
        mediaRecorder?.apply {
            stop()
            delay(1000)
            release()
        }
        callWhisper()
    }

    private suspend fun callWhisper() {
        println("Calling Whisper API...")
        val audioFile = File(filePath)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "recording.mp3", RequestBody.create("audio/mpeg".toMediaTypeOrNull(), audioFile))
            .addFormDataPart("model", "whisper-1")
            .addFormDataPart("prompt", "The transcript is about OpenAI which makes technology like DALL·E, GPT-3, and ChatGPT with the hope of one day building an AGI system that benefits all of humanity.")
            .build()

        val request = Request.Builder()
            .url("https://api.openai.com/v1/audio/transcriptions")
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        val response = client.newCall(request).await()
        if (response.isSuccessful) {
            val resultText = response.body?.string() ?: ""
            if (resultText == "") {
                println("Error with calling Whisper: empty response body")
            }

            val json = JSONObject(resultText)
            val text = json.optString("text", "")
            transcription.emit(text)
        } else {
            // Handle error response
            println("Error with calling Whisper: status not 200")
        }

    }

    private fun playRecording() {
        println("Playing recording...")
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
        }
    }
}
