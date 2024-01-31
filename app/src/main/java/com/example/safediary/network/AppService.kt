package com.example.safediary.network

import com.example.safediary.diary.list.EntryDTO
import com.example.safediary.dto.FaceLoginDto
import com.example.safediary.dto.PinLoginDto
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppService(private val client: HttpClient) {

    suspend fun registerWithFace(faceLoginDto: FaceLoginDto): HttpResponse =
        withContext(Dispatchers.IO) {
            return@withContext client.post("users/update") {
                contentType(ContentType.Application.Json)
                setBody(faceLoginDto)
            }
        }

    suspend fun loginWithFace(faceLoginDto: FaceLoginDto): HttpResponse =
        withContext(Dispatchers.IO) {
            return@withContext client.post("users/login") {
                contentType(ContentType.Application.Json)
                setBody(faceLoginDto)
            }
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

    suspend fun getEntries(): HttpResponse {
        return client.get("diary-pages") {
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun getEntry(id: Int): HttpResponse {
        return client.get("diary-pages/$id") {
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun postEntry(entryDTO: EntryDTO): HttpResponse {
        return client.post("diary-pages") {
            contentType(ContentType.Application.Json)
            setBody(entryDTO)
        }
    }

    suspend fun putEntry(entryDTO: EntryDTO): HttpResponse {
        return client.put("diary-pages/${entryDTO.id}") {
            contentType(ContentType.Application.Json)
            setBody(entryDTO)
        }
    }

    suspend fun deleteEntry(id: Int): HttpResponse {
        return client.delete("diary-pages/$id") {
            contentType(ContentType.Application.Json)
        }
    }
}


