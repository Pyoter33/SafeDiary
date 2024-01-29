package com.example.safediary.diary.view

import java.time.LocalDate

data class ViewEntryState(
    val id: Int = 0,
    val title: String = "",
    val date: LocalDate = LocalDate.now(),
    val content: String = "",
    val deleteDialogVisible: Boolean = false,
    val isLoading: Boolean = false
)
