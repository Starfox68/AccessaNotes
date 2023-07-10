package com.shaphr.accessanotes

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Environment
import android.text.StaticLayout
import android.text.TextPaint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class FileCompiler {
    private val psPerInch = 72
    private val pageWidth = (9.5 * psPerInch).toInt()
    private val margins = 1 * psPerInch
    private val filePath = "${Environment.getExternalStorageDirectory()}/Download"

    fun getPDF(title: String, text: String): PdfDocument {
        val titlePaint = TextPaint()
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 24F
        titlePaint.textAlign = Paint.Align.CENTER

        val bodyPaint = TextPaint()
        bodyPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        bodyPaint.textSize = 16F

        val layout = StaticLayout.Builder.obtain(
            text, 0, text.length, bodyPaint, pageWidth - 2 * margins
        ).setLineSpacing(2F, 1F).build()

        val doc = PdfDocument()
        val page = doc.startPage(
            PageInfo.Builder(
                pageWidth, (layout.height + 2.5 * margins + titlePaint.textSize).toInt(),
                1
            ).create()
        )
        val canvas = page.canvas

        // Draw title at centre of canvas
        canvas.drawText(title, canvas.width / 2F, 1.5F * margins, titlePaint)
        // Draw text below title
        canvas.translate(1F * margins, 1.5F * margins + titlePaint.textSize)
        layout.draw(canvas)

        doc.finishPage(page)
        return doc
    }

    fun writePDF(doc: PdfDocument, title: String) {
        val file = File(filePath, "$title.pdf")

        try {
            println("Writing file to $filePath")
            doc.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            println("Writing file failed")
            e.printStackTrace()
        }
    }
}
