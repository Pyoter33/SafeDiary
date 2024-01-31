package com.example.safediary.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FaceLoginDto (
    @SerializedName("uuid")
    val uuid: String,
    @SerializedName("face-image")
    val faceImageBase64: String
)