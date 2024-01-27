package com.example.safediary.diary.create_edit

import java.time.LocalDate

data class CreateEditEntryState(
    val title: String = "",
    val date: LocalDate = LocalDate.now(),
    val content: String = "",
    val isLoading: Boolean = false,
    val isListening: Boolean = false
)
