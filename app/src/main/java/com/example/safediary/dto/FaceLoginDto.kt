package com.example.safediary.dto

data class FaceLoginDto (
    val deviceId: String,
    val faceImageBytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FaceLoginDto

        if (deviceId != other.deviceId) return false
        return faceImageBytes.contentEquals(other.faceImageBytes)
    }

    override fun hashCode(): Int {
        var result = deviceId.hashCode()
        result = 31 * result + faceImageBytes.contentHashCode()
        return result
    }
}