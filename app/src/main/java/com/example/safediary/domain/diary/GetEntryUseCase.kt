package com.example.safediary.domain.diary

import com.example.safediary.network.AppService
import com.example.safediary.ui.diary.list.EntryDTO
import com.example.safediary.ui.diary.list.GetEntryResult
import com.example.safediary.utils.toBodyOrError

class GetEntryUseCase(private val appService: AppService) {

    suspend operator fun invoke(id: Int): EntryDTO {
        return appService.getEntry(id).toBodyOrError<GetEntryResult>().obj
    }
}