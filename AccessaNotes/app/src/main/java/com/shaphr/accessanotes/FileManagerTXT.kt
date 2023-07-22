package com.shaphr.accessanotes

import java.io.File
import java.io.IOException

class FileManagerTXT : FileManagerAbstract() {

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