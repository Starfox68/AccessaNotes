package com.shaphr.accessanotes.ui.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class StartAndEndScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val mutableTitle: MutableStateFlow<String> = MutableStateFlow("")
    val title: StateFlow<String> = mutableTitle

    private val mutablePrompt: MutableStateFlow<String> = MutableStateFlow("")
    val prompt: StateFlow<String> = mutablePrompt

    private val mutableStart: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val canStart: StateFlow<Boolean> = mutableStart

    private val mutableFileText: MutableStateFlow<String> = MutableStateFlow("")
    val fileText: StateFlow<String> = mutableFileText

    init { }

    fun readFile(uri: Uri) {
        val mimeType = getApplication<Application>().contentResolver.getType(uri)
        val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)

        when (mimeType) {
            "text/plain" -> {
                // read from .txt file
                println("Reading text file...")
                val text = inputStream?.bufferedReader()?.use { it.readText() }
                if (text != null) {
                    mutableFileText.value = text
                }
            }
            "application/pdf" -> {
                // read from .pdf file
                println("Reading PDF file...")
                val file = getFileFromUri(uri)
                if (file != null) {
                    uploadPdfFile(file)
                } else {
                    println("Error: Invalid PDF file")
                }
            }
            // add cases for other file types here
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        val context = getApplication<Application>()
        val file = File(context.cacheDir, "temp_file.pdf")

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun uploadPdfFile(file: File) {
        println("Uploading PDF file ${file.name} ...")
        val url = "https://api.pdf.co/v1/file/upload"
        val apiKey = "mhmohebbi@gmail.com_17073ef6740486dd9a58fdfcb2d377ab6e07bc45894b6e3d167526053d91031133df7b3b"

        GlobalScope.launch {
            try {
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        file.name,
                        file.asRequestBody("application/pdf".toMediaType())
                    )
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .header("x-api-key", apiKey)
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = responseBody?.let { JSONObject(it) }
                    val fileUrl = jsonResponse?.getString("url")
                    // Call getPDFText function with the extracted URL
                    if (fileUrl != null) {
                        getPDFText(fileUrl)
                    }
                } else {
                    // Handle the error
                    println("Error from response: ${response.code} ${response.message}")
                }
            } catch (e: Exception) {
                // Handle the exception
                println("Error: ${e.message}")
            }
        }
    }

    fun getPDFText(url: String) {
        val apiUrl = "https://api.pdf.co/v1/pdf/convert/to/text-simple"
        val apiKey = "mhmohebbi@gmail.com_17073ef6740486dd9a58fdfcb2d377ab6e07bc45894b6e3d167526053d91031133df7b3b"

        GlobalScope.launch {
            try {
                val requestBody = JSONObject().apply {
                    put("url", url)
                    put("inline", true)
                    put("async", false)
                }

                val request = Request.Builder()
                    .url(apiUrl)
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val client = OkHttpClient()
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = responseBody?.let { JSONObject(it) }
                    val text = jsonResponse?.getString("body")
                    if (text != null) {
                        mutableFileText.value = text
                    }
                } else {
                    // Handle the error
                    println("Error: ${response.code} ${response.message}")
                }
            } catch (e: Exception) {
                // Handle the exception
                println("Error: ${e.message}")
            }
        }
    }

    fun setPrompt(text: String) {
        mutablePrompt.value = text
    }

    fun setTitle(text: String) {
        mutableTitle.value = text
        mutableStart.value = title.value.isNotEmpty()
    }
}
