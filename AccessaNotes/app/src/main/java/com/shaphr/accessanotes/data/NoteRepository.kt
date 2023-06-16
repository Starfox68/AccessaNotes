package com.shaphr.accessanotes.data

import com.shaphr.accessanotes.data.sources.NoteSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NoteRepository @Inject constructor(
    private val noteSource: NoteSource
) {
    val favoriteLatestNews: Flow<Note> =
        noteSource.noteFlow
}