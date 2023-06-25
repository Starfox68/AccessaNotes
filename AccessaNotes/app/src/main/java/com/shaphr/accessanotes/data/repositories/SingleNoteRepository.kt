package com.shaphr.accessanotes.data.repositories

import com.shaphr.accessanotes.data.models.Note
import com.shaphr.accessanotes.data.sources.NoteSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SingleNoteRepository @Inject constructor(
    private val noteSource: NoteSource
) {
    val notes: Flow<Note> =
        noteSource.noteFlow
}