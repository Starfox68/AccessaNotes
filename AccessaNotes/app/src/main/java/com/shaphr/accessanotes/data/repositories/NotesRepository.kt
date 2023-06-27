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
            Note(2, "Test Note 2", LocalDate.now(), "This is a test note. I am writing a bunch of text here so that when we do our demonstration you guys have something to look at. I am trying to make it seem like there's a lot of text here but if you look too closely you will see that I'm actually repeating myself. I actually feel like I do that quite a bit anyway, but I'm just happy to be here"),
            Note(3, "Test Note 3", LocalDate.now(), "Test Content!")
        )
    }
}