package com.shaphr.accessanotes

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Environment
import android.text.StaticLayout
import android.text.TextPaint
import org.apache.poi.wp.usermodel.HeaderFooterType
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class FileCompiler {
    private val filePath = "${Environment.getExternalStorageDirectory()}/Download"
    private val disclaimer = "This notes document was generated with AccessaNotes from an audio " +
            "recording using AI. Information may be inaccurate. Use at your own caution."
    private val titleSize = 24
    private val bodySize = 16
    private val disclaimerSize = 14
    private val psPerInch = 72
    private val pageWidth = (9.5 * psPerInch).toInt()
    private val margins = 1 * psPerInch


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

    fun toPDF(title: String, text: String) {
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

        val layout = StaticLayout.Builder.obtain(
            text, 0, text.length, bodyPaint, pageWidth - 2 * margins
        ).setLineSpacing(2F, 1F).build()
        val disclaimerLayout = StaticLayout.Builder.obtain(
            disclaimer, 0, disclaimer.length, disclaimerPaint, pageWidth - 2 * margins
        ).setLineSpacing(2F, 1F).build()

        val doc = PdfDocument()
        val page = doc.startPage(
            PageInfo.Builder(
                pageWidth, (layout.height + 2.5 * margins + titlePaint.textSize).toInt(),
                1
            ).create()
        )
        val canvas = page.canvas

        // Draw disclaimer
        canvas.translate(canvas.width / 2F, 0.4F * margins)
        disclaimerLayout.draw(canvas)
        canvas.translate(-canvas.width / 2F, -0.4F * margins)
        // Draw title at centre of canvas
        canvas.drawText(title, canvas.width / 2F, 1.5F * margins, titlePaint)
        // Draw text below title
        canvas.translate(1F * margins, 1.5F * margins + titlePaint.textSize)
        layout.draw(canvas)

        doc.finishPage(page)
        writePDF(doc, title)
    }

    fun toTXT(title: String, text: String) {
        val file = File(filePath, "$title.txt")

        try {
            println("Writing TXT file to $filePath")
            file.writeText("$disclaimer\n\n"+ "$title\n\n" + text)
        } catch (e: IOException) {
            println("Writing TXT file failed")
            e.printStackTrace()
        }
    }

    fun toDOCX(title: String, text: String) {
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
        val bodyRun = doc.createParagraph().createRun()
        bodyRun.fontSize = bodySize
        bodyRun.setText(text)

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
