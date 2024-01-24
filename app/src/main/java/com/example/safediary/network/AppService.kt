package com.example.safediary.network

import com.example.safediary.dto.FaceLoginDto
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.request.HttpResponseData
import io.ktor.client.statement.DefaultHttpResponse
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.InternalAPI
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AppService(private val client: HttpClient) {

    @OptIn(InternalAPI::class)
    suspend fun loginWithFace(faceLoginDto: FaceLoginDto): HttpResponse = withContext(Dispatchers.IO) {
        delay(2000)
        return@withContext DefaultHttpResponse(HttpClientCall(client), HttpResponseData(HttpStatusCode(200, "OK"), GMTDate.START, Headers.Empty, HttpProtocolVersion.HTTP_1_0, "123", Dispatchers.IO) )
    }
}