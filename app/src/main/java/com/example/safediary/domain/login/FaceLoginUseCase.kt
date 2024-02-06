package com.example.safediary.domain.login

import android.graphics.Bitmap
import com.example.safediary.dto.FaceLoginDto
import com.example.safediary.network.AppService
import com.example.safediary.utils.Constants
import com.example.safediary.utils.generateBase64String
import com.example.safediary.utils.toBodyOrError

class FaceLoginUseCase(private val appService: AppService) {

    suspend operator fun invoke(image: Bitmap, appId: String): String? {
        val imageBase64 = image.generateBase64String()
        val result = appService.loginWithFace(FaceLoginDto(appId, imageBase64))
        result.toBodyOrError<Unit>()
        return result.headers[Constants.AUTHORIZATION_HEADER]
    }
}