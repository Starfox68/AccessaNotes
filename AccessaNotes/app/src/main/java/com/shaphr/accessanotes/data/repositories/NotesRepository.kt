package com.shaphr.accessanotes.data.repositories


import android.app.Application
import androidx.lifecycle.LiveData
import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.data.database.NoteDataAccess
import com.shaphr.accessanotes.data.database.NoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepository @Inject constructor(private val application: Application) {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val dao: NoteDataAccess = NoteDatabase.getDatabase(application).getNoteDataAccess()

    fun getNotes(): LiveData<List<Note>> {
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