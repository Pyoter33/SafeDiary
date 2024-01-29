package com.example.safediary.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PinLoginDto (
    @SerializedName("uuid")
    val uuid: String,
    @SerializedName("pin")
    val pin: String
)