package com.shaphr.accessanotes.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Data Access Object (DAO) for the Note and NoteItem database.
 * Provides methods for performing operations on the database like insert, update, delete, and query.
 */
@Dao
interface NoteDataAccess {
    /**
     * Insert a new note into the Note table.
     * If a note with the same ID already exists in the database, ignore this operation.
     *
     * @param note the note object to be inserted.
     * @return the row ID of the newly inserted note.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Note::class)
    suspend fun insert(note: Note): Long

    /**
     * Update a specific note in the Note table.
     *
     * @param note the note object to be updated.
     */
    @Update(entity = Note::class)
    suspend fun update(note: Note)

    /**
     * Delete a specific note from the Note table.
     *
     * @param note the note object to be deleted.
     */
    @Delete(entity = Note::class)
    suspend fun delete(note: Note)

    /**
     * Delete a note by its id from the Note table.
     *
     * @param id the id of the note to be deleted.
     */
    @Query("DELETE FROM `Note` WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    /**
     * Retrieve all notes from the Note table and order them by id in ascending order.
     *
     * @return a Flow list of all notes.
     */
    @Query("SELECT * FROM `Note` ORDER BY id ASC")
    fun getNotes(): Flow<List<Note>>

    // Methods for NoteItem

    /**
     * Insert a new note item into the note_items table.
     * If a note item with the same ID already exists in the database, ignore this operation.
     *
     * @param noteItem the note item object to be inserted.
     * @return the row ID of the newly inserted note item.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = NoteItem::class)
    suspend fun insertNoteItem(noteItem: NoteItem): Long

    /**
     * Update a specific note item in the note_items table.
     *
     * @param noteItem the note item object to be updated.
     */
    @Update(entity = NoteItem::class)
    suspend fun updateNoteItem(noteItem: NoteItem)

    /**
     * Delete a specific note item from the note_items table.
     *
     * @param noteItem the note item object to be deleted.
     */
    @Delete(entity = NoteItem::class)
    suspend fun deleteNoteItem(noteItem: NoteItem)

    /**
     * Retrieve all note items from the note_items table and order them by item_order in ascending order.
     *
     * @return a Flow list of all note items.
     */
    @Query("SELECT * FROM `note_items` ORDER BY item_order ASC")
    fun getNoteItems(): Flow<List<NoteItem>>

}
