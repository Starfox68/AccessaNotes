package com.shaphr.accessanotes.data

import com.shaphr.accessanotes.data.sources.NoteSource
import kotlinx.coroutines.flow.Flow


class NoteRepository(
    private val noteSource: NoteSource
) {
    val favoriteLatestNews: Flow<Note> =
            noteSource.noteFlow
}