package com.shaphr.accessanotes

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import ru.gildor.coroutines.okhttp.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
        context.getExternalFilesDir(null)?.absolutePath + "/AccessaNotes_recording.mp3"
    }
    private var recordingJob: Job? = null

    // Function to start audio recording
    fun startRecording() {
        println("Starting recording...")
        println("Audio File path: $filePath")

        // Configure MediaRecorder and start recording
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(filePath)
            prepare()
            start()
        }

        // Schedule the recording job to stop after 5 minutes (300000 milliseconds) and restart recording
        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            delay(300000) // 5 minutes
            stopRecording()
            // Restart recording
            startRecording()
        }
    }

    // Function to stop audio recording
    suspend fun stopRecording() {
        println("Stopping recording...")
        mediaRecorder?.apply {
            stop()
            delay(1000)
            release()
        }

        // Check if the device is connected to a network
        if (isConnectedToWifi() || isConnectedToNetwork()) {
            callWhisper() // Call the Whisper API for transcription
        } else {
            showToast("You're Offline! Recording Downloaded...")
            saveRecordingToFile() // Save the recording locally if the device is offline
        }
        recordingJob?.cancel() // Cancel the recording job
    }

    // Function to call the Whisper API for transcription using a provided file URI
    suspend fun callWhisper(fileUri: Uri) {
        println("Calling Whisper API...")

        // Read the audio file from the provided URI
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(fileUri)
        val requestBodyFile = inputStream?.let { inputStream ->
            RequestBody.create(
                "audio/mpeg".toMediaTypeOrNull(),
                inputStream.readBytes()
            )
        }

        // Check if the audio file was successfully read
        if (requestBodyFile == null) {
            println("Error reading file")
            return
        }

        // Build the request body with audio file, model, and prompt
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "recording.mp3", requestBodyFile)
            .addFormDataPart("model", "whisper-1")
            .addFormDataPart(
                "prompt", "The transcript is about OpenAI which makes technology like DALL·E, GPT-3, and ChatGPT with the hope of one day building an AGI system that benefits all of humanity."
            )
            .build()

        // Create the HTTP request with the Whisper API endpoint and necessary headers
        val request = Request.Builder()
            .url("https://api.openai.com/v1/audio/transcriptions")
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        // Execute the API call asynchronously
        val response = client.newCall(request).await()

        // Handle the API response
        if (response.isSuccessful) {
            val resultText = response.body?.string() ?: ""
            if (resultText == "") {
                println("Error with calling Whisper: empty response body")
            }

            val json = JSONObject(resultText)
            val text = json.optString("text", "")
            transcription.emit(text) // Emit the transcription result
        } else {
            println(response.message)
            // Handle error response
            println("Error with calling Whisper: status not 200")
        }
    }

    // Function to call the Whisper API for transcription using the file path of the recording
    suspend fun callWhisper(filePath: String = this.filePath) {
        println("Calling Whisper API...")

        // Read the audio file from the provided file path
        val audioFile = File(filePath)

        // Build the request body with audio file, model, and prompt
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "recording.mp3", RequestBody.create("audio/mpeg".toMediaTypeOrNull(), audioFile))
            .addFormDataPart(
                "model", "whisper-1"
            )
            .addFormDataPart(
                "prompt", "The transcript is about OpenAI which makes technology like DALL·E, GPT-3, and ChatGPT with the hope of one day building an AGI system that benefits all of humanity."
            )
            .build()

        // Create the HTTP request with the Whisper API endpoint and necessary headers
        val request = Request.Builder()
            .url("https://api.openai.com/v1/audio/transcriptions")
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        // Execute the API call asynchronously
        val response = client.newCall(request).await()

        // Handle the API response
        if (response.isSuccessful) {
            val resultText = response.body?.string() ?: ""
            if (resultText == "") {
                println("Error with calling Whisper: empty response body")
            }

            val json = JSONObject(resultText)
            val text = json.optString("text", "")
            transcription.emit(text) // Emit the transcription result
        } else {
            println(response.message)
            // Handle error response
            println("Error with calling Whisper: status not 200")
        }

    }

    // Function to save the recording to a file in the device's Download directory
    private fun saveRecordingToFile() {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val audioFile = File(filePath)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileNameWithDateTime = "${audioFile.nameWithoutExtension}_${timeStamp}.${audioFile.extension}"
        val newFile = File(downloadsDir, fileNameWithDateTime)
        audioFile.copyTo(newFile, overwrite = true)
        println("Recording saved to ${newFile.absolutePath}")
    }

    // Function to check if the device is connected to any network
    private fun isConnectedToNetwork(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            // for other device how are able to connect with Ethernet
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            // for check internet over Bluetooth
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    // Function to check if the device is connected to a Wi-Fi network
    private fun isConnectedToWifi(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI
    }

    // Function to show a toast message regarding the recording
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    // Function to play the recorded audio
    private fun playRecording() {
        println("Playing recording...")
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
        }
    }
}
