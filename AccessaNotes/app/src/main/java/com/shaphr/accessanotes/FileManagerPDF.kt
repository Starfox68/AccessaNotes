package com.shaphr.accessanotes

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.text.StaticLayout
import android.text.TextPaint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class FileManagerPDF @Inject constructor(application: Application) : FileManagerAbstract() {
    private val context = application
    private val psPerInch = 72
    private val pageWidth = (9.5 * psPerInch).toInt()
    private val pageHeight = 11 * psPerInch
    private val margins = 1 * psPerInch

    override fun getFile(uri: Uri): File {
        // Get the File object from the Uri
        val file = getFileFromUri(uri)
        if (file != null) {
            return file
        }
        throw IOException("File not found")
    }

    // NOTE: We are using PDF.co as a service for extracting PDF text as built in Android libraries are not sufficient in support
    override suspend fun readFile(file: Any): String {
        println("Reading PDF file...")
        // First we must upload the PDF file to PDF.co and get the URL of where it is stored in the response
        val url: String? = uploadPdfFile(file as File)
        if (url != null) {
            // Then we can use the URL to get the text from the PDF file using PDF.co
            val text = getPDFText(url)
            return text!!
        }
        throw IOException("File not found")
    }

    // Helper function to retrieve a File object from the given Uri using the context's cache directory
    private fun getFileFromUri(uri: Uri): File? {
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

    // Coroutine function to upload the PDF file to the specified URL from pdf.co
    private suspend fun uploadPdfFile(file: File): String? {
        println("Uploading PDF file...")
        val url = "https://api.pdf.co/v1/file/upload"
        val apiKey = "mhmohebbi@gmail.com_17073ef6740486dd9a58fdfcb2d377ab6e07bc45894b6e3d167526053d91031133df7b3b"

        return withContext(Dispatchers.Default) {
            try {
                // Create the multipart request body with the PDF file
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        file.name,
                        file.asRequestBody("application/pdf".toMediaType())
                    )
                    .build()

                // Build the HTTP request with the API key and the request body
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
                    // If the response is successful, extract the URL from the JSON response and return it
                    val responseBody = response.body?.string()
                    val jsonResponse = responseBody?.let { JSONObject(it) }
                    val fileUrl = jsonResponse?.getString("url")
                    fileUrl
                } else {
                    // Handle the error if the response is not successful
                    println("Error from response: ${response.code} ${response.message}")
                    null
                }
            } catch (e: Exception) {
                // Handle exceptions that may occur during the API call
                println("Error: ${e.message}")
                null
            }
        }
    }

    // Coroutine function to get the text content of the PDF using pdf.co
    private suspend fun getPDFText(url: String): String? {
        val apiUrl = "https://api.pdf.co/v1/pdf/convert/to/text-simple"
        val apiKey = "mhmohebbi@gmail.com_17073ef6740486dd9a58fdfcb2d377ab6e07bc45894b6e3d167526053d91031133df7b3b"

        return withContext(Dispatchers.IO) {
            try {
                // Create the JSON request body with the PDF URL and other parameters
                val requestBody = JSONObject().apply {
                    put("url", url)
                    put("inline", true)
                    put("async", false)
                }

                // Build the HTTP request with the API key and the JSON request body
                val request = Request.Builder()
                    .url(apiUrl)
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    // If the response is successful, extract the text content from the JSON response and return it
                    val responseBody = response.body?.string()
                    val jsonResponse = responseBody?.let { JSONObject(it) }
                    return@withContext jsonResponse?.getString("body") // The text content is in the "body" field
                } else {
                    // Handle the error if the response is not successful
                    println("Error: ${response.code} ${response.message}")
                    return@withContext null
                }
            } catch (e: Exception) {
                // Handle exceptions that may occur during the API call
                println("Error: ${e.message}")
                return@withContext null
            }
        }
    }


    private fun getPDFPaints(): Triple<TextPaint, TextPaint, TextPaint> {
        // Formatting for each part
        val titlePaint = TextPaint()
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = titleSize.toFloat()
        titlePaint.textAlign = Paint.Align.CENTER

        val bodyPaint = TextPaint()
        bodyPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        bodyPaint.textSize = bodySize.toFloat()

        val disclaimerPaint = TextPaint()
        disclaimerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        disclaimerPaint.textSize = disclaimerSize.toFloat()
        disclaimerPaint.textAlign = Paint.Align.CENTER

        return Triple(titlePaint, bodyPaint, disclaimerPaint)
    }

    private fun makePDFPage(
        doc: PdfDocument,
        nextPageNum: Int,
        disclaimerLayout: StaticLayout
    ): PdfDocument.Page {
        val page = doc.startPage(PageInfo.Builder(pageWidth, pageHeight, nextPageNum).create())
        val canvas = page.canvas

        // Draw disclaimer
        canvas.translate(pageWidth / 2F, 0.4F * margins)
        disclaimerLayout.draw(canvas)
        canvas.translate(-pageWidth / 2F, -0.4F * margins)

        return page
    }

    override fun createDoc(title: String, content: List<Any>): Any {
        val (titlePaint, bodyPaint, disclaimerPaint) = getPDFPaints()
        // Static layout for disclaimer header
        val disclaimerLayout = StaticLayout.Builder.obtain(
            disclaimer, 0, disclaimer.length, disclaimerPaint, pageWidth - 2 * margins
        ).setLineSpacing(2F, 1F).build()

        val doc = PdfDocument()
        var nextPageNum = 1
        var page = makePDFPage(doc, nextPageNum, disclaimerLayout)
        var canvas = page.canvas
        nextPageNum += 1

        // Draw title at centre of canvas
        canvas.drawText(title, pageWidth / 2F, 1.5F * margins, titlePaint)

        // Draw text below title
        canvas.translate(1F * margins, 1.5F * margins + titlePaint.textSize)
        var curHeight = 1.5F * margins + titlePaint.textSize // Current height of page
        content.forEach {
            var height = 0F
            var layout: StaticLayout? = null
            if (it is String) {
                // Create static layout for text if string
                layout = StaticLayout.Builder.obtain(
                    it, 0, it.length, bodyPaint, pageWidth - 2 * margins
                ).setLineSpacing(2F, 1F).build()
                height = layout.height.toFloat()
            } else if (it is Bitmap) {
                // Set to image height
                height = imageHeight
            }

            // Make new page if new content will not fit on existing one based on height
            if (curHeight + height >= pageHeight - 2 * margins) {
                doc.finishPage(page)
                page = makePDFPage(doc, nextPageNum, disclaimerLayout)
                canvas = page.canvas
                nextPageNum += 1

                canvas.translate(1F * margins, 1.2F * margins)
                curHeight = 0.2F
            }

            // Draw layout with text or bitmap based on type
            if (it is String) {
                layout?.draw(canvas)
            } else if (it is Bitmap) {
                canvas.drawBitmap(it, (pageWidth - imageWidth) / 2 - margins, 0F, null)
            }

            // Move canvas to position for next item and update current height
            canvas.translate(0F, height + bodyPaint.textSize)
            curHeight += height + bodyPaint.textSize
        }

        doc.finishPage(page)
        return doc
    }

    override fun writeDoc(title: String, doc: Any) {
        try {
            println("Writing PDF file to $filePath")
            (doc as PdfDocument).writeTo(FileOutputStream("$filePath/$title.pdf"))
        } catch (e: IOException) {
            println("Writing PDF file failed")
            e.printStackTrace()
        }
    }
}
