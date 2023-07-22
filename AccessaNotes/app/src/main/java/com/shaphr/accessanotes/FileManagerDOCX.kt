package com.shaphr.accessanotes

import android.graphics.Bitmap
import org.apache.poi.util.Units
import org.apache.poi.wp.usermodel.HeaderFooterType
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class FileManagerDOCX : FileManagerAbstract() {

    override fun createDoc(title: String, content: List<Any>): Any {
        val doc = XWPFDocument()

        // Disclaimer
        val header = doc.createHeader(HeaderFooterType.DEFAULT)
        val headerRun = header.createParagraph().createRun()
        headerRun.fontSize = disclaimerSize
        headerRun.isItalic = true
        headerRun.setText(disclaimer)

        // Title
        val titleParagraph = doc.createParagraph()
        titleParagraph.alignment = ParagraphAlignment.CENTER
        val titleRun = titleParagraph.createRun()
        titleRun.fontSize = titleSize
        titleRun.isBold = true
        titleRun.setText(title)

        // Body text/images
        content.forEach {
            val paragraph = doc.createParagraph()
            val bodyRun = paragraph.createRun()

            if (it is String) {
                bodyRun.fontSize = bodySize
                bodyRun.setText(it)

            } else if (it is Bitmap) {
                paragraph.alignment = ParagraphAlignment.CENTER
                // Write bitmap to temp file so that we can open FileInputStream
                FileOutputStream("$filePath/temp.jpeg").use { out ->
                    it.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }
                val iStream = FileInputStream("$filePath/temp.jpeg")
                bodyRun.addPicture(
                    iStream, XWPFDocument.PICTURE_TYPE_JPEG, "temp.jpeg",
                    Units.toEMU(imageWidth.toDouble()), Units.toEMU(imageHeight.toDouble())
                )
                iStream.close()
            }
        }
        try {
            File("$filePath/temp.jpeg").delete() // Delete temp file
        } catch (e: IOException) {
            println("Delete temp file failed")
            e.printStackTrace()
        }

        return doc
    }

    override fun writeDoc(title: String, doc: Any) {
        try {
            println("Writing DOCX file to $filePath")
            (doc as XWPFDocument).write(FileOutputStream("$filePath/$title.docx"))
        } catch (e: IOException) {
            println("Writing DOCX file failed")
            e.printStackTrace()
        }
    }
}