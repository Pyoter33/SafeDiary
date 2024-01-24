package com.example.safediary.dto

import kotlinx.serialization.Serializable

@Serializable
data class FaceLoginDto (
    val deviceId: String,
    val faceImage: String
)