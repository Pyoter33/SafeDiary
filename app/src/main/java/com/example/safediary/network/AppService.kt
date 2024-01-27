package com.example.safediary.network

import com.example.safediary.dto.FaceLoginDto
import com.example.safediary.dto.PinLoginDto
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppService(private val client: HttpClient) {

    suspend fun registerWithFace(faceLoginDto: FaceLoginDto): HttpResponse =
        withContext(Dispatchers.IO) {
            return@withContext client.submitFormWithBinaryData(url = "users/update",
                formData = formData {
                    append("face-image", faceLoginDto.faceImageBytes, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=face-image")
                    })
                    append("uuid", faceLoginDto.uuid)
                }
            )
        }

    suspend fun registerWithPin(pinLoginDto: PinLoginDto): HttpResponse {
            return client.post("users/register") {
                contentType(ContentType.Application.Json)
                setBody(pinLoginDto)
            }
        }

    suspend fun loginWithPin(pinLoginDto: PinLoginDto): HttpResponse {
        return client.post("users/login") {
            contentType(ContentType.Application.Json)
            setBody(pinLoginDto)
        }
    }
}


