package com.shaphr.accessanotes.data.models

import android.graphics.Bitmap
import java.time.LocalDate

/**
 * Data class representing a note in the UI. This class holds all data necessary for displaying a note in the UI.
 *
 * @param title The title of the note.
 * @param summarizeContent A short summary of the note content.
 * @param transcript The full text of the note.
 * @param date The date of the note.
 * @param id The unique identifier of the note.
 * @param items A list of items associated with the note.
 */
data class UiNote(
    val title: String = "",
    val summarizeContent: String = "",
    val transcript: String = "",
    val date: LocalDate? = null,
    var id: Int = 0,
    val items: List<UiNoteItem>? = null,
)

/**
 * Data class representing an item of a note in the UI. This class holds all data necessary for displaying a note item in the UI.
 *
 * @param id The unique identifier of the note item.
 * @param noteId The unique identifier of the note this item belongs to.
 * @param imageTrue A boolean flag indicating if this item is an image.
 * @param content The content of the note item.
 * @param bitmap If this item is an image, this is the bitmap of the image.
 * @param order The order of this item in the note.
 */
data class UiNoteItem(
    val id: Int = 0,
    val noteId: Int = 0,
    val imageTrue: Boolean,
    val content: String?,
    val bitmap: Bitmap?,
    val order: Int,
)
