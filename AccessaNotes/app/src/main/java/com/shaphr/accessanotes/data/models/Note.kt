package com.shaphr.accessanotes.data.models

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.shaphr.accessanotes.data.database.NoteItem
import java.time.LocalDate
import java.util.Date

data class UiNote(
    val title: String = "",
    val content: String = "",
    val transcript: String = "",
    val date: LocalDate? = null,
    val notifyAt: Long = Date().time,
    var id: Int = 0,
    val items: List<UiNoteItem>? = null,
)
data class UiNoteItem(
    val id: Int,
    val noteId: Int,
    val imageTrue: Boolean,
    val content: String?,
    val bitmap: Bitmap?,
    val order: Int,
)
