package com.example.safediary.diary.list

import java.time.LocalDate

data class Entry(
    val title: String,
    val date: LocalDate,
    val content: String
)
