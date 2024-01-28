package com.example.safediary.diary.view

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class ViewEntryState(
    val title: String,
    val date: LocalDate,
    val content: String,
    val deleteDialogVisible: Boolean = false
): Parcelable
