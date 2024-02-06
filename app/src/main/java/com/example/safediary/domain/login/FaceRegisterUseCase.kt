package com.example.safediary.domain.login

import android.graphics.Bitmap
import com.example.safediary.dto.FaceLoginDto
import com.example.safediary.network.AppService
import com.example.safediary.utils.generateBase64String
import com.example.safediary.utils.toBodyOrError

class FaceRegisterUseCase(private val appService: AppService) {

    suspend operator fun invoke(image: Bitmap, appId: String) {
        val imageBase64 = image.generateBase64String()
        appService.registerWithFace(FaceLoginDto(appId, imageBase64)).toBodyOrError<Unit>()
    }
}