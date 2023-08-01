package com.shaphr.accessanotes

import android.net.Uri
import android.os.Environment


abstract class FileManagerAbstract {

    protected val filePath = "${Environment.getExternalStorageDirectory()}/Download"
    protected val disclaimer = "This notes document was generated through AccessaNotes from an " +
            "audio recording using AI. Information may be inaccurate. Use at your own caution."
    protected val titleSize = 24
    protected val bodySize = 16
    protected val disclaimerSize = 14
    protected val imageWidth = 300F
    protected val imageHeight = 100F

    suspend fun importFile(uri: Uri): String {
       return readFile(getFile(uri))
    }

    fun exportNote(title: String, content: List<Any>) {
        val doc = createDoc(title, content)
        writeDoc(title, doc)
    }

    // Abstract functions for importing a file
    protected abstract fun getFile(uri: Uri): Any
    protected abstract suspend fun readFile(file: Any): String

    // Abstract functions for exporting a file
    // Content must be list of strings/bitmap images
    protected abstract fun createDoc(title: String, content: List<Any>): Any
    protected abstract fun writeDoc(title: String, doc: Any)
}
