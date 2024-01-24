package com.example.safediary.network

import com.example.safediary.SharedPreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sharedPreferencesHelper: SharedPreferencesHelper): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()


        val newRequest = originalRequest.newBuilder()
            .header(AUTHORIZATION_HEADER, "Bearer ${sharedPreferencesHelper.token}")
            .header("Content-Type", "application/json")
            .build()

        return chain.proceed(newRequest)
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
    }
}