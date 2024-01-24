package com.example.safediary.login.pin_login

data class PinLoginState(
    val pin: String = "",
    val isLoginEnabled: Boolean = false,
    val isLoading: Boolean = false
)