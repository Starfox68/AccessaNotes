package com.shaphr.accessanotes.data.sources

import com.shaphr.accessanotes.data.models.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NoteSource @Inject constructor() {
    var isSessionStarted: Boolean = false
    val noteFlow: Flow<Note> = flow {
        emit(Note(0))
    }

    fun onRecordingStart() {
        // TODO Call and listen to API service
        isSessionStarted = true
    }
}

