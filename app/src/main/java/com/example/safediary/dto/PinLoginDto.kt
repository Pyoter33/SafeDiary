package com.example.safediary.dto

import kotlinx.serialization.Serializable

@Serializable
data class PinLoginDto (
    val uuid: String,
    val pin: String
)