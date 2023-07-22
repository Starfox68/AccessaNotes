package com.shaphr.accessanotes

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Environment
import android.text.StaticLayout
import android.text.TextPaint
import org.apache.poi.util.Units.toEMU
import org.apache.poi.wp.usermodel.HeaderFooterType
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class FileCompiler {
    private val filePath = "${Environment.getExternalStorageDirectory()}/Download"
    private val disclaimer = "This notes document was generated through AccessaNotes from an " +
            "audio recording using AI. Information may be inaccurate. Use at your own caution."
    private val titleSize = 24
    private val bodySize = 16
    private val disclaimerSize = 14
    private val psPerInch = 72
    private val pageWidth = (9.5 * psPerInch).toInt()
    private val pageHeight = 11 * psPerInch
    private val margins = 1 * psPerInch
    private val imageWidth = 300F
    private val imageHeight = 100F


    private fun writePDF(doc: PdfDocument, name: String) {
        val file = File(filePath, "$name.pdf")

        try {
            println("Writing PDF file to $filePath")
            doc.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            println("Writing PDF file failed")
            e.printStackTrace()
        }
    }

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

    fun toPDF(title: String, text: List<String>) {
        val (titlePaint, bodyPaint, disclaimerPaint) = getPDFPaints()

        val disclaimerLayout = StaticLayout.Builder.obtain(
            disclaimer, 0, disclaimer.length, disclaimerPaint, pageWidth - 2 * margins
        ).setLineSpacing(2F, 1F).build()
        val bodyLayouts = text.map {
            StaticLayout.Builder.obtain(
                it, 0, it.length, bodyPaint, pageWidth - 2 * margins
            ).setLineSpacing(2F, 1F).build()
        }

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
        bodyLayouts.forEach {
            if (curHeight + it.height >= pageHeight - 1 * margins) {
                doc.finishPage(page)
                page = makePDFPage(doc, nextPageNum, disclaimerLayout)
                canvas = page.canvas
                nextPageNum += 1

                canvas.translate(1F * margins, 1.2F * margins)
                curHeight = 0.2F
            }

            it.draw(canvas)
            canvas.translate(0F, it.height + bodyPaint.textSize)
            curHeight += it.height + bodyPaint.textSize
        }

//        canvas.drawBitmap(bmp, 100F, 100F, null)

        doc.finishPage(page)
        writePDF(doc, title)
    }

    fun toTXT(title: String, content: List<Any>) {
        val file = File(filePath, "$title.txt")
        val text = content.filterIsInstance<String>()

        try {
            println("Writing TXT file to $filePath")
            file.writeText((listOf(disclaimer, title) + text).joinToString(separator = "\n\n"))
        } catch (e: IOException) {
            println("Writing TXT file failed")
            e.printStackTrace()
        }
    }

    fun toDOCX(title: String, content: List<Any>) {
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

        // Body text
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
                bodyRun.addPicture(
                    FileInputStream("$filePath/temp.jpeg"), XWPFDocument.PICTURE_TYPE_JPEG,
                    "temp.jpeg", toEMU(imageWidth.toDouble()), toEMU(imageHeight.toDouble())
                )
            }
        }
        File("$filePath/temp.jpeg").delete() // Delete temp file

        val file = File(filePath, "$title.docx")
        try {
            println("Writing DOCX file to $filePath")
            doc.write(FileOutputStream(file))
        } catch (e: IOException) {
            println("Writing DOCX file failed")
            e.printStackTrace()
        }
    }
}
