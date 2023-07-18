package com.shaphr.accessanotes.data.database

import java.io.Serializable
import java.util.Date
import androidx.room.*
import kotlinx.serialization.json.JsonNames
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }
}


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