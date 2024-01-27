package com.example.safediary.dto

data class FaceLoginDto (
    val uuid: String,
    val faceImageBytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FaceLoginDto

        if (uuid != other.uuid) return false
        return faceImageBytes.contentEquals(other.faceImageBytes)
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + faceImageBytes.contentHashCode()
        return result
    }
}