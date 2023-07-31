package com.shaphr.accessanotes.data.repositories


import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.data.database.NoteDataAccess
import com.shaphr.accessanotes.data.database.NoteDatabase
import com.shaphr.accessanotes.data.database.NoteItem
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
            refreshNotes()
//            val uiNotes = dao.getNotes()
//                .flatMapConcat { notes ->
//                    println("Number of notes from dao.getNotes: ${notes.size}")
//                    flowsOfNotesAndItems(notes)
//                }
//                .map { (note, noteItems) ->
//                    println("Creating UiNote for note id: ${note.id}")
//                    createUiNote(note, noteItems)
//                }
////                .toList()
//                .collect {
//                    _notes.value = _notes.value + it
//                }
////            println("Number of UiNotes created: ${uiNotes.size}")
////            _notes.value = uiNotes
        }
    }

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

//        val uiNotes = dao.getNotes()
//            .flatMapConcat { notes ->
//                println("Number of notes from dao.getNotes: ${notes.size}")
//                flowsOfNotesAndItems(notes)
//            }
//            .map { (note, noteItems) ->
//                println("Creating UiNote for note id: ${note.id}")
//                createUiNote(note, noteItems)
//            }
//
////            .toList()
//            .collect {
//                _notes.value = _notes.value + it
//            }
//        println("Number of UiNotes created: ${uiNotes.size}")
//        _notes.value = uiNotes
    }

//    private fun flowsOfNotesAndItems(notes: List<Note>): Flow<Pair<Note, List<NoteItem>>> = flow {
//        for (note in notes) {
//            emitAll(
//                dao.getNoteItems(note.id)
//                    .map { noteItems ->
//                        println("Number of note items for note id ${note.id}: ${noteItems.size}")
//                        note to noteItems
//                    }
//            )
//        }
//    }

    private fun createUiNote(notes: List<Pair<Note, List<NoteItem>>>): List<UiNote> {
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

    fun setNote(uiNote: UiNote) = ioScope.launch {
        println("came to set note")
        val note = Note(uiNote.title, uiNote.summarizeContent, uiNote.transcript, uiNote.date)
        val noteId = dao.insert(note).toInt()
        uiNote.items?.forEach { uiNoteItem ->
            val imagePath = if (uiNoteItem.imageTrue) saveBitmapToFile(uiNoteItem.bitmap!!) else null
            val noteItem = NoteItem(noteId, uiNoteItem.imageTrue, uiNoteItem.content, imagePath, uiNoteItem.order)
            dao.insertNoteItem(noteItem)
        }
    }

    fun updateNote(uiNote: UiNote) = ioScope.launch {
        val note = Note(uiNote.title, uiNote.summarizeContent, uiNote.transcript, uiNote.date, uiNote.id)
        dao.update(note)
        uiNote.items?.forEach { uiNoteItem ->
            val imagePath = if (uiNoteItem.imageTrue) saveBitmapToFile(uiNoteItem.bitmap!!) else null
            val noteItem = NoteItem(uiNote.id, uiNoteItem.imageTrue, uiNoteItem.content, imagePath, uiNoteItem.order, uiNoteItem.id)
            dao.updateNoteItem(noteItem)
        }
    }

    fun deleteNote(uiNote: UiNote) = ioScope.launch {
        val note = Note(uiNote.title, uiNote.summarizeContent, uiNote.transcript, uiNote.date, uiNote.id)
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

typealias NoteWithItems = Pair<Note, List<NoteItem>>
