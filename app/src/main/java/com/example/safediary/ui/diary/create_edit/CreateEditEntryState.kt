package com.example.safediary.ui.diary.create_edit

import java.time.LocalDate

data class CreateEditEntryState(
    val id: Int = 0,
    val title: String = "",
    val date: LocalDate = LocalDate.now(),
    val content: String = "",
    val isLoading: Boolean = false,
    val isListening: Boolean = false
)
