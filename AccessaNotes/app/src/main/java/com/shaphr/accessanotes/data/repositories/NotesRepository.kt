package com.shaphr.accessanotes.data.repositories


import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.data.database.NoteDataAccess
import com.shaphr.accessanotes.data.database.NoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepository @Inject constructor(private val application: Application) {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val dao: NoteDataAccess
    init {
        // initiate database access
        dao = NoteDatabase.getDatabase(application).getNoteDataAccess()
    }
    fun getNotes(): LiveData<List<Note>> {
        // TODO - create a source + local database to store notes locally
        val notes = listOf(
            Note("Test Note 1", "Test Content!", LocalDate.now()),
            Note("Test Note 2", "This is a test note. I am writing a bunch of text here so that when we do our demonstration you guys have something to look at. I am trying to make it seem like there's a lot of text here but if you look too closely you will see that I'm actually repeating myself. I actually feel like I do that quite a bit anyway, but I'm just happy to be here", LocalDate.now()),
            Note("Test Note 3", "Test Content!", LocalDate.now())
        )
        for (note in notes) {
            setNote(note)
        }
        return dao.getNotes()
    }

    fun setNote(note: Note) = ioScope.launch {
        dao.insert(note)
    }

    fun updateNote(note: Note) = ioScope.launch {
        dao.update(note)
    }

    fun deleteNote(note: Note) = ioScope.launch {
        dao.delete(note)
    }
}