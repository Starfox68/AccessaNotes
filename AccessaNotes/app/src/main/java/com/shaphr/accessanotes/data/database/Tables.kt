package com.shaphr.accessanotes.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate

@Entity(
    tableName = "Note",
)
data class Note(
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "summarize_content") val content: String = "",
    @ColumnInfo(name = "transcript") val transcript: String = "",
    @ColumnInfo(name = "date") val date: LocalDate? = null,
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
) : Serializable


@Entity(tableName = "note_items")
data class NoteItem(
    @ColumnInfo(name = "note_id") var noteId: Int,
    @ColumnInfo(name = "image_true") var imageTrue: Boolean,
    @ColumnInfo(name = "content") var content: String?,
    @ColumnInfo(name = "image_path") var imagePath: String?,
    @ColumnInfo(name = "item_order") var itemOrder: Int,
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
)
