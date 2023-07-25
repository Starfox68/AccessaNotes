package com.shaphr.accessanotes.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
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
    suspend fun deleteNoteById(id: Long)

    @Query("SELECT * FROM `Note` ORDER BY id ASC")
    fun getNotes(): Flow<List<Note>>

    // Methods for NoteItem
    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = NoteItem::class)
    suspend fun insertNoteItem(noteItem: NoteItem): Long

    @Update(entity = NoteItem::class)
    suspend fun updateNoteItem(noteItem: NoteItem)

    @Delete(entity = NoteItem::class)
    suspend fun deleteNoteItem(noteItem: NoteItem)

    @Query("SELECT * FROM `note_items` WHERE note_id = :noteId ORDER BY item_order ASC")
    fun getNoteItems(noteId: Int): Flow<List<NoteItem>>

}
