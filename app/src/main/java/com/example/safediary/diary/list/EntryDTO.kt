package com.example.safediary.diary.list

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class GetEntriesResult(
    @SerializedName("diary_pages")
    val list: List<EntryDTO>
)

@Serializable
data class GetEntryResult(
    @SerializedName("diary_page")
    val obj: EntryDTO
)

@Serializable
data class EntryDTO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("creation_date")
    val creationDate: String,
    @SerializedName("content")
    val content: String
)
