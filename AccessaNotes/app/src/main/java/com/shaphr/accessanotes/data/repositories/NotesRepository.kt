package com.shaphr.accessanotes.data.repositories

import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.data.database.NoteDataAccess
import com.shaphr.accessanotes.data.database.NoteDatabase
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepository @Inject constructor() {
//    private val dao: NoteDataAccess = NoteDatabase.getNoteDataAccess()

    fun getNotes(): List<Note> {
        // TODO - create a source + local database to store notes locally
        return listOf(
            Note("Test Note 1", "Test Content!"),
            Note("Test Note 2", "Test Content!"),
            Note("Test Note 3", "Test Content!")
        )
    }
}