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

    @Query("DELETE FROM `Note` WHERE id = :id")
    fun deleteNoteById(id: Long)

    @Query("SELECT * FROM `Note` ORDER BY id ASC")
    fun getNotes(): LiveData<List<Note>>

    @Transaction
    @Query("SELECT * FROM `Note` WHERE id = :noteId")
    fun getNoteWithItems(noteId: Int): LiveData<NoteImage>

}