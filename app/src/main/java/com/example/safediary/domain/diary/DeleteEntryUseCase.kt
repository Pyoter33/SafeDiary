package com.example.safediary.domain.diary

import com.example.safediary.network.AppService
import com.example.safediary.utils.toBodyOrError

class DeleteEntryUseCase(private val appService: AppService) {

    suspend operator fun invoke(id: Int) {
        appService.deleteEntry(id).toBodyOrError<Unit>()
    }
}