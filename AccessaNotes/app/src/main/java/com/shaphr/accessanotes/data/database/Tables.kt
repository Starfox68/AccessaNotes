package com.shaphr.accessanotes.data.database

import java.io.Serializable
import java.util.Date
import androidx.room.*
import kotlinx.serialization.json.JsonNames
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import androidx.room.TypeConverter

@Entity(
    tableName = "Note",
)

data class Note(
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "summarize_note") val summarizeContent: String = "",
    @ColumnInfo(name = "transcript") val transcript: String = "",
    @ColumnInfo(name = "date") val date: LocalDate? = null,
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
) : Serializable