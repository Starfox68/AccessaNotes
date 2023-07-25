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
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.BitmapFactory
import com.shaphr.accessanotes.data.database.NoteItem
import com.shaphr.accessanotes.data.models.UiNote
import com.shaphr.accessanotes.data.models.UiNoteItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Singleton
class NotesRepository @Inject constructor(private val application: Application) {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val dao: NoteDataAccess
    init {
        // initiate database access
        dao = NoteDatabase.getDatabase(application).getNoteDataAccess()
    }

    private val _notes = MutableStateFlow<List<UiNote>>(emptyList())
    val notes: StateFlow<List<UiNote>> = _notes
    init {
        ioScope.launch {
            dao.getNotes().collect { notes ->
                _notes.value = notes.map { note ->
                    val uiNoteItems = mutableListOf<UiNoteItem>()
                    dao.getNoteItems(note.id).collect { noteItems ->
                        uiNoteItems.addAll(noteItems.map { noteItem ->
                            val bitmap = if (noteItem.imageTrue) loadImageFromPath(noteItem.imagePath ?: "") else null
                            UiNoteItem(noteItem.id, noteItem.noteId, noteItem.imageTrue, noteItem.content, bitmap, noteItem.itemOrder)
                        })
                    }
                    UiNote(note.title, note.content, note.transcript, note.date, note.notifyAt, note.id, uiNoteItems)
                }
            }
        }
    }

    suspend fun refreshNotes() {
        dao.getNotes().collect { notes ->
            _notes.value = notes.map { note ->
                val uiNoteItems = mutableListOf<UiNoteItem>()
                dao.getNoteItems(note.id).collect { noteItems ->
                    uiNoteItems.addAll(noteItems.map { noteItem ->
                        val bitmap = if (noteItem.imageTrue) loadImageFromPath(noteItem.imagePath ?: "") else null
                        UiNoteItem(noteItem.id, noteItem.noteId, noteItem.imageTrue, noteItem.content, bitmap, noteItem.itemOrder)
                    })
                }
                UiNote(note.title, note.content, note.transcript, note.date, note.notifyAt, note.id, uiNoteItems)
            }
        }
    }


    fun setNote(uiNote: UiNote) = ioScope.launch {
        val note = Note(uiNote.title, uiNote.content, uiNote.transcript, uiNote.date, uiNote.notifyAt, uiNote.id)
        val noteId = dao.insert(note).toInt()
        uiNote.items?.forEach { uiNoteItem ->
            val imagePath = if (uiNoteItem.imageTrue) saveBitmapToFile(uiNoteItem.bitmap!!) else null
            val noteItem = NoteItem(uiNoteItem.id, noteId, uiNoteItem.imageTrue, uiNoteItem.content, imagePath, uiNoteItem.order)
            dao.insertNoteItem(noteItem)
        }
    }

    fun updateNote(uiNote: UiNote) = ioScope.launch {
        val note = Note(uiNote.title, uiNote.content, uiNote.transcript, uiNote.date, uiNote.notifyAt, uiNote.id)
        dao.update(note)
        uiNote.items?.forEach { uiNoteItem ->
            val imagePath = if (uiNoteItem.imageTrue) saveBitmapToFile(uiNoteItem.bitmap!!) else null
            val noteItem = NoteItem(uiNoteItem.id, uiNote.id, uiNoteItem.imageTrue, uiNoteItem.content, imagePath, uiNoteItem.order)
            dao.updateNoteItem(noteItem)
        }
    }

    fun deleteNote(uiNote: UiNote) = ioScope.launch {
        val note = Note(uiNote.title, uiNote.content, uiNote.transcript, uiNote.date, uiNote.notifyAt, uiNote.id)
        dao.delete(note)
    }

    private fun saveBitmapToFile(bitmap: Bitmap): String {
        val filename = "${System.currentTimeMillis()}.jpg"
        val file = File(application.getExternalFilesDir(null), filename)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file.absolutePath
    }

    private fun loadImageFromPath(path: String): Bitmap {
        return BitmapFactory.decodeFile(path)
    }
}
