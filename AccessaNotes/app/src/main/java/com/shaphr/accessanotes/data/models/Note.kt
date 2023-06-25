package com.shaphr.accessanotes.data.models

import java.time.LocalDate

data class Note(
    val id: Int,
    val title: String = "",
    val date: LocalDate? = null,
    val content: String = ""
)