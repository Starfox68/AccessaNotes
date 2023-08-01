package com.shaphr.accessanotes

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class FileManagerTXT @Inject constructor(application: Application) : FileManagerAbstract() {
    private val contentResolver = application.contentResolver

    @SuppressLint("Recycle")
    override fun getFile(uri: Uri): Any {
        // Get input stream from file URI and return it
        val inputStream = contentResolver.openInputStream(uri)
        if (inputStream != null) {
            return inputStream
        }
        throw IOException("Could not open file")
    }

    // Assumes file is a text file, promise fulfilled and checked in instantiation during upload
    override suspend fun readFile(file: Any): String {
        println("Reading text file...")
        // Read text from input stream as is and return it
        return (file as InputStream).bufferedReader().use { it.readText() }
    }
    override fun createDoc(title: String, content: List<Any>): Any {
        val text = content.filterIsInstance<String>() // TXT only supports strings, no images
        return (listOf(disclaimer, title) + text).joinToString(separator = "\n\n")
    }

    override fun writeDoc(title: String, doc: Any) {
        try {
            println("Writing TXT file to $filePath")
            File(filePath, "$title.txt").writeText(doc as String)
        } catch (e: IOException) {
            println("Writing TXT file failed")
            e.printStackTrace()
        }
    }
}
