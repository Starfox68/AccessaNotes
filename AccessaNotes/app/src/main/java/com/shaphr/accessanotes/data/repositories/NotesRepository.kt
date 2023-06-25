package com.shaphr.accessanotes.data.repositories

import com.shaphr.accessanotes.data.models.Note
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepository @Inject constructor() {

    fun getNotes(): List<Note> {
        // TODO - create a source + local database to store notes locally
        return listOf(
            Note(1, "Test Note 1", LocalDate.now(), "Test Content!"),
            Note(2, "Test Note 2", LocalDate.now(), "Test Content!"),
            Note(3, "Test Note 3", LocalDate.now(), "Test Content!")
        )
    }
}