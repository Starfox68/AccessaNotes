package com.shaphr.accessanotes.model

import com.shaphr.accessanotes.data.NoteRepository


class NoteModel(
    private val noteRepository: NoteRepository,
) {
    fun onSessionStart() {

    }
}