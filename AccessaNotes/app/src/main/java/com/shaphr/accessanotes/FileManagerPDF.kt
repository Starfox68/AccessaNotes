package com.shaphr.accessanotes

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.text.StaticLayout
import android.text.TextPaint
import java.io.FileOutputStream
import java.io.IOException

class FileManagerPDF : FileManagerAbstract() {

    private val psPerInch = 72
    private val pageWidth = (9.5 * psPerInch).toInt()
    private val pageHeight = 11 * psPerInch
    private val margins = 1 * psPerInch

    private fun getPDFPaints(): Triple<TextPaint, TextPaint, TextPaint> {
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
        var curHeight = 1.5F * margins + titlePaint.textSize
        content.forEach {
            var height = 0F
            var layout: StaticLayout? = null
            if (it is String) {
                layout = StaticLayout.Builder.obtain(
                    it, 0, it.length, bodyPaint, pageWidth - 2 * margins
                ).setLineSpacing(2F, 1F).build()
                height = layout.height.toFloat()
            } else if (it is Bitmap) {
                height = imageHeight
            }

            if (curHeight + height >= pageHeight - 2 * margins) {
                doc.finishPage(page)
                page = makePDFPage(doc, nextPageNum, disclaimerLayout)
                canvas = page.canvas
                nextPageNum += 1

                canvas.translate(1F * margins, 1.2F * margins)
                curHeight = 0.2F
            }

            if (it is String) {
                layout?.draw(canvas)
            } else if (it is Bitmap) {
                canvas.drawBitmap(it, (pageWidth - imageWidth) / 2 - margins, 0F, null)
            }

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
