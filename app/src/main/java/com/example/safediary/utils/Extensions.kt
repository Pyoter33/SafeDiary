package com.example.safediary.utils

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

data class HttpRequestException(val errorCode: Int, val errorMessage: String): Exception()

suspend inline fun <reified T> HttpResponse.toBodyOrError(): T {
    return if (status.isSuccess()) {
        body()
    } else {
        throw HttpRequestException(status.value, bodyAsText())
    }
}