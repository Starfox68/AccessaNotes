package com.shaphr.accessanotes.data.models

import android.graphics.Bitmap
import java.time.LocalDate
import java.util.Date

data class UiNote(
    val title: String = "",
    val summarizeContent: String = "",
    val transcript: String = "",
    val date: LocalDate? = null,
    var id: Int = 0,
    val items: List<UiNoteItem>? = null,
)

data class UiNoteItem(
    val id: Int = 0,
    val noteId: Int = 0,
    val imageTrue: Boolean,
    val content: String?,
    val bitmap: Bitmap?,
    val order: Int,
)
