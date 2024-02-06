package com.example.safediary.domain.diary

import com.example.safediary.network.AppService
import com.example.safediary.ui.diary.list.EntryDTO
import com.example.safediary.utils.toBodyOrError

class UpdateEntryUseCase(private val appService: AppService) {

    suspend operator fun invoke(entryDTO: EntryDTO) {
        appService.putEntry(entryDTO).toBodyOrError<Unit>()
    }
}