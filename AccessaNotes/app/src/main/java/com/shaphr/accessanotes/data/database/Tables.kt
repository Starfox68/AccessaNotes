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
    @ColumnInfo(name = "content") val content: String = "",
    @ColumnInfo(name = "date") val date: LocalDate? = null,
    @ColumnInfo(name = "notifyAt") val notifyAt: Long = Date().time,
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
) : Serializable