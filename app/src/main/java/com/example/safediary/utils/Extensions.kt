package com.example.safediary.utils

import android.graphics.Bitmap
import android.util.Base64
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import java.io.ByteArrayOutputStream

data class HttpRequestException(val errorCode: Int, val errorMessage: String): Exception()

suspend inline fun <reified T> HttpResponse.toBodyOrError(): T {
    return if (status.isSuccess()) {
        body()
    } else {
        throw HttpRequestException(status.value, bodyAsText())
    }
}

fun Bitmap.generateBase64String(): String {
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val b = baos.toByteArray()
    return Base64.encodeToString(b, Base64.NO_WRAP)
}