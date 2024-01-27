package com.example.safediary.network

import com.example.safediary.SharedPreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sharedPreferencesHelper: SharedPreferencesHelper): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = sharedPreferencesHelper.token?.let {
           originalRequest.newBuilder()
                .header(AUTHORIZATION_HEADER, it)
                .build()
        } ?: originalRequest
        return chain.proceed(newRequest)
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
    }
}