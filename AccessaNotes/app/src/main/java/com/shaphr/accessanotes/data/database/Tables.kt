package com.shaphr.accessanotes.data.database

import java.io.Serializable
import java.util.Date
import androidx.room.*
import kotlinx.serialization.json.JsonNames
import java.text.SimpleDateFormat
import java.util.*

@Entity(
    tableName = "Note",
)
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "date") val notifyAt: Long = Date().time,
//    @ColumnInfo(name = "folderId") val folderId: Long?,
//    @ColumnInfo(name = "createdAt") val createdAt: Long = Date().time,
//    @ColumnInfo(name = "updatedAt") val updatedAt: Long = Date().time,
) : Serializable
