package com.example.safediary.dto

import kotlinx.serialization.Serializable

@Serializable
data class PinLoginDto (
    val deviceId: String,
    val pin: String
)