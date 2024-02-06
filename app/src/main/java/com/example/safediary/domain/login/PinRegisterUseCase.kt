package com.example.safediary.domain.login

import com.example.safediary.dto.PinLoginDto
import com.example.safediary.network.AppService
import com.example.safediary.utils.Constants
import com.example.safediary.utils.toBodyOrError

class PinRegisterUseCase(private val appService: AppService) {

    suspend operator fun invoke(appId: String, pin: String): String? {
        val result = appService.registerWithPin(PinLoginDto(appId, pin))
        result.toBodyOrError<Unit>()
        return result.headers[Constants.AUTHORIZATION_HEADER]
    }
}