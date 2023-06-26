package com.shaphr.accessanotes.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.text.SimpleDateFormat
import java.util.*

@Dao
interface NoteDataAccess {
    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Note::class)
    suspend fun insert(note: Note): Long

    @Update(entity = Note::class)
    suspend fun update(note: Note)

    @Delete(entity = Note::class)
    suspend fun delete(note: Note)

    @Query("delete from `Note` where id = :id")
    fun deleteNoteById(id: Long)

    // *** this is the point of modification for filter and ordering feature
    @Query("Select * from `Note` order by id ASC")
    fun getNotes(): LiveData<List<Note>>

    @Query("Select * from Note where updatedAt > :timestamp")
    suspend fun getNoteSince(timestamp: Long): List<Note>
}