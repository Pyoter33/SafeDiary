package com.example.safediary.domain.diary

import com.example.safediary.network.AppService
import com.example.safediary.ui.diary.list.EntryDTO
import com.example.safediary.ui.diary.list.GetEntriesResult
import com.example.safediary.utils.toBodyOrError

class GetAllEntriesUseCase(private val appService: AppService) {

    suspend operator fun invoke(): List<EntryDTO> {
        return appService.getEntries().toBodyOrError<GetEntriesResult>().list
    }
}