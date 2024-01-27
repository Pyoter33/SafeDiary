package com.example.safediary.diary.create_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safediary.network.AppService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class CreateEditEntryViewModel(appService: AppService, savedStateHandle: SavedStateHandle): ViewModel() {

    private val _createEditEntryState = MutableStateFlow(CreateEditEntryState())
    val createEditEntryState = _createEditEntryState.asStateFlow()

    private val _createEditEntryChannel = Channel<CreateEditEntryUIEvent>()
    val createEditEntryChannelFlow = _createEditEntryChannel.receiveAsFlow()

    fun onEvent(event: CreateEditEntryEvent) {
        when (event) {
            CreateClickedEvent -> {
                val currentState =createEditEntryState.value
                if (currentState.content.isEmpty() || currentState.title.isEmpty()) {
                    viewModelScope.launch {
                        _createEditEntryChannel.send(DataNotFilledUIEvent)

                    }
                    return
                }

                // create new entry
                viewModelScope.launch {
                    _createEditEntryChannel.send(EntryAddedUIEvent)
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

sealed class CreateEditEntryUIEvent
data object EntryAddedUIEvent: CreateEditEntryUIEvent()
data object DataNotFilledUIEvent: CreateEditEntryUIEvent()