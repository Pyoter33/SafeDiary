package com.example.safediary.diary.list

data class EntriesState(
    val searchText: String = "",
    val entries: List<Entry> = emptyList(),
    val isLoading: Boolean = false
)
