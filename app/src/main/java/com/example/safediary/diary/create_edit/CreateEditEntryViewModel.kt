package com.example.safediary.diary.create_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safediary.Constants
import com.example.safediary.Constants.DATE_PATTERN
import com.example.safediary.Constants.DEFAULT_INT_ARG
import com.example.safediary.diary.list.EntryDTO
import com.example.safediary.diary.list.GetEntryResult
import com.example.safediary.network.AppService
import com.example.safediary.utils.toBodyOrError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CreateEditEntryViewModel(private val appService: AppService, savedStateHandle: SavedStateHandle): ViewModel() {

    private val argId by lazy { savedStateHandle.get<Int>(Constants.ARG_ENTRY_ID).takeIf { it != DEFAULT_INT_ARG } }

    private val _createEditEntryState = MutableStateFlow(
        CreateEditEntryState()
    )
    val createEditEntryState = _createEditEntryState.asStateFlow()

    private val _createEditEntryChannel = Channel<CreateEditEntryUIEvent>()
    val createEditEntryChannelFlow = _createEditEntryChannel.receiveAsFlow()

    fun onEvent(event: CreateEditEntryEvent) {
        when (event) {
            CreateClickedEvent -> {
                _createEditEntryState.update {  state ->
                    state.copy(isLoading = true)
                }
                val currentState = createEditEntryState.value
                if (currentState.content.isEmpty() || currentState.title.isEmpty()) {
                    viewModelScope.launch {
                        _createEditEntryChannel.send(DataNotFilledUIEvent)
                    }
                    return
                }
                val entryDTO = EntryDTO(0, currentState.title, currentState.date.format(
                    DateTimeFormatter.ofPattern(DATE_PATTERN)), currentState.content)

                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        argId?.let { id ->
                            appService.putEntry(entryDTO.copy(id = id)).toBodyOrError<Unit>()
                        } ?: run {
                            appService.postEntry(entryDTO).toBodyOrError<Unit>()
                        }
                        _createEditEntryChannel.send(EntryAddedUIEvent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _createEditEntryChannel.send(SomethingWentWrongCreateUIEvent)
                    }
                    _createEditEntryState.update {  state ->
                        state.copy(isLoading = false)
                    }
                }
            }
            is DateChangedEvent -> {
                _createEditEntryState.update { state ->
                    state.copy(date = event.date)
                }
            }
            is ContentChangedEvent -> {
                _createEditEntryState.update { state ->
                    state.copy(content = event.content)
                }
            }
            is TitleChangedEvent -> {
                _createEditEntryState.update { state ->
                    state.copy(title = event.title)
                }
            }

            is VoiceListeningChanged -> {
                _createEditEntryState.update { state ->
                    state.copy(isListening = event.isListening)
                }
            }

            is ContentChangedBySpeechEvent -> {
                _createEditEntryState.update { state ->
                    if (state.content.isEmpty()) {
                        state.copy(content = event.content)
                    } else {
                        state.copy(content = "${state.content} ${event.content}")
                    }
                }
            }

            GetDataCreateEvent -> {
                _createEditEntryState.update {  state ->
                    state.copy(isLoading = true)
                }
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val id = argId ?: throw IllegalStateException()
                        val entry = appService.getEntry(id).toBodyOrError<GetEntryResult>()
                        _createEditEntryState.update { state ->
                            with(entry.obj) {
                                state.copy(
                                    id = id,
                                    title = title,
                                    date = LocalDate.parse(
                                        creationDate,
                                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                    ),
                                    content = content
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _createEditEntryChannel.send(SomethingWentWrongCreateUIEvent)
                    }
                    _createEditEntryState.update {  state ->
                        state.copy(isLoading = false)
                    }
                }
            }
        }

    }
}

sealed class CreateEditEntryEvent
data class ContentChangedEvent(val content: String): CreateEditEntryEvent()
data class ContentChangedBySpeechEvent(val content: String): CreateEditEntryEvent()
data class TitleChangedEvent(val title: String): CreateEditEntryEvent()
data class DateChangedEvent(val date: LocalDate): CreateEditEntryEvent()
data class VoiceListeningChanged(val isListening: Boolean): CreateEditEntryEvent()
data object CreateClickedEvent: CreateEditEntryEvent()
data object GetDataCreateEvent: CreateEditEntryEvent()

sealed class CreateEditEntryUIEvent
data object EntryAddedUIEvent: CreateEditEntryUIEvent()
data object DataNotFilledUIEvent: CreateEditEntryUIEvent()
data object SomethingWentWrongCreateUIEvent: CreateEditEntryUIEvent()