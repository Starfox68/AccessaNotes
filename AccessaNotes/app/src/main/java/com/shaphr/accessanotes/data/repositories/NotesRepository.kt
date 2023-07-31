package com.shaphr.accessanotes.data.repositories


import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.shaphr.accessanotes.data.database.NoteDataAccess
import com.shaphr.accessanotes.data.database.NoteDatabase
import com.shaphr.accessanotes.data.models.UiNote
import com.shaphr.accessanotes.data.models.UiNoteItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.data.database.NoteItem

@Singleton
class NotesRepository @Inject constructor(private val application: Application) {
    // This is the I/O dispatcher scope for coroutines
    private val ioScope = CoroutineScope(Dispatchers.IO)

    // Data Access Object for the notes
    private val dao: NoteDataAccess

    // Create an instance of the NoteDatabase and get the NoteDataAccess
    init {
        dao = NoteDatabase.getDatabase(application).getNoteDataAccess()
    }

    // MutableStateFlow is a state-holder observable that emits updates to its collectors in a conflation-safe way.
    // Here it is initialized with an empty list of UiNote.
    private val _notes = MutableStateFlow<List<UiNote>>(emptyList())

    // StateFlow is a read-only version of MutableStateFlow, this is exposed for consumers to listen to the note updates
    val notes: StateFlow<List<UiNote>> = _notes

    init {
        ioScope.launch {
            refreshNotes()
        }
    }

    // Function to refresh the list of notes
    suspend fun refreshNotes() {
        dao.getNotes().combine(dao.getNoteItems()) { notes, noteItems ->
            val notesAndItems = notes.map { note ->
                Pair(note, noteItems.filter { it.id == note.id })
            }
            notesAndItems
        }.map {
            createUiNote(it)
        }.collect {
            _notes.value = it
        }
    }

    // Function to convert note data into a UI friendly model
    private fun createUiNote(notes: List<Pair<Note, List<NoteItem>>>): List<UiNote> {
        // Mapping from the database Note model to the UI model
        return notes.map { noteData ->
            val note = noteData.first
            val noteItems = noteData.second

            val uiNoteItems = noteItems.map { noteItem ->
                val bitmap =
                    if (noteItem.imageTrue) loadImageFromPath(noteItem.imagePath ?: "") else null
                UiNoteItem(
                    noteItem.id,
                    noteItem.noteId,
                    noteItem.imageTrue,
                    noteItem.content,
                    bitmap,
                    noteItem.itemOrder
                )
            }
            UiNote(note.title, note.content, note.transcript, note.date, note.id, uiNoteItems)
        }
    }

    // Function to save a new note
    fun setNote(uiNote: UiNote) = ioScope.launch {
        val note = Note(uiNote.title, uiNote.summarizeContent, uiNote.transcript, uiNote.date)
        val noteId = dao.insert(note).toInt()
        uiNote.items?.forEach { uiNoteItem ->
            val imagePath = if (uiNoteItem.imageTrue) saveBitmapToFile(uiNoteItem.bitmap!!) else null
            val noteItem = NoteItem(noteId, uiNoteItem.imageTrue, uiNoteItem.content, imagePath, uiNoteItem.order)
            dao.insertNoteItem(noteItem)
        }
    }

    // Function to update an existing note
    fun updateNote(uiNote: UiNote) = ioScope.launch {
        val note = Note(uiNote.title, uiNote.summarizeContent, uiNote.transcript, uiNote.date, uiNote.id)
        dao.update(note)
        uiNote.items?.forEach { uiNoteItem ->
            val imagePath = if (uiNoteItem.imageTrue) saveBitmapToFile(uiNoteItem.bitmap!!) else null
            val noteItem = NoteItem(uiNote.id, uiNoteItem.imageTrue, uiNoteItem.content, imagePath, uiNoteItem.order, uiNoteItem.id)
            dao.updateNoteItem(noteItem)
        }
    }

    // Function to delete a note
    fun deleteNote(uiNote: UiNote) = ioScope.launch {
        val note = Note(uiNote.title, uiNote.summarizeContent, uiNote.transcript, uiNote.date, uiNote.id)
        dao.delete(note)
    }

    // Function to save a bitmap image to a file and returns the file path

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
// Pair of Note and its corresponding List of NoteItem
typealias NoteWithItems = Pair<Note, List<NoteItem>>
