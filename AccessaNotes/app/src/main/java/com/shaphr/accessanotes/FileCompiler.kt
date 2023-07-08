package com.shaphr.accessanotes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Environment
import android.text.StaticLayout
import android.text.TextPaint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class FileCompiler {

    fun toPDF(title: String, text: String) {
        val doc = PdfDocument()
        val pageInfo: PageInfo = PageInfo.Builder(684, 792, 1).create()
        val page: Page = doc.startPage(pageInfo)

        val canvas: Canvas = page.canvas
        val titlePaint = TextPaint()
        val bodyPaint = TextPaint()

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 24F
        titlePaint.textAlign = Paint.Align.CENTER

        canvas.drawText(title, canvas.width.toFloat()/2, 98F, titlePaint)
        canvas.translate(72F, 114F)

        bodyPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        bodyPaint.textSize = 16F

        val sb = StaticLayout.Builder.obtain(text, 0, text.length, bodyPaint,
            canvas.width-144).setLineSpacing(2F, 1F)
        sb.build().draw(canvas)

        doc.finishPage(page)

        val filePath = "${Environment.getExternalStorageDirectory()}/Download"
        val file = File(filePath, "test.pdf")

        try {
            println("Writing file to $filePath")
            doc.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            println("Writing file failed")
            e.printStackTrace()
        }

        doc.close()
    }
}