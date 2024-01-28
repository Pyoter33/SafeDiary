package com.example.safediary.diary.view

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safediary.Constants
import com.example.safediary.Constants.ARG_ENTRY_CONTENT
import com.example.safediary.Constants.ARG_ENTRY_DATE
import com.example.safediary.Constants.ARG_ENTRY_TITLE
import com.example.safediary.network.AppService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ViewEntryViewModel(appService: AppService, savedStateHandle: SavedStateHandle) : ViewModel() {

    private val argTitle by lazy { savedStateHandle.get<String>(ARG_ENTRY_TITLE) }
    private val argDate by lazy {
        savedStateHandle.get<String>(ARG_ENTRY_DATE)?.let {
            LocalDate.parse(
                it, DateTimeFormatter.ofPattern(
                    Constants.DATE_PATTERN
                )
            )
        }
    }
    private val argContent by lazy { savedStateHandle.get<String>(ARG_ENTRY_CONTENT) }

    private val _viewEntryState = MutableStateFlow(
        ViewEntryState(
            argTitle ?: "Title",
            argDate ?: LocalDate.of(2024, 11, 24),
            argContent ?: "Some text"
        )
    )
    val viewEntryStateFlow = _viewEntryState.asStateFlow()

    private val _viewEntryChannel = Channel<ViewEntryUIEvent>()
    val viewEntryChannelFlow = _viewEntryChannel.receiveAsFlow()

    fun onEvent(event: ViewEntryEvent) {
        when (event) {
            DeleteClickedEvent -> {
                _viewEntryState.update { state ->
                    state.copy(deleteDialogVisible = true)
                }
            }

            DeleteConfirmedEvent -> {
                _viewEntryState.update { state ->
                    state.copy(deleteDialogVisible = false)
                }
                viewModelScope.launch {
                    //delete
                    _viewEntryChannel.send(NavigateBackEvent)
                }
            }

            DeleteRejectedEvent -> {
                _viewEntryState.update { state ->
                    state.copy(deleteDialogVisible = false)
                }
            }

            EditClickedEvent -> {
                viewModelScope.launch {
                    _viewEntryChannel.send(NavigateToEditEvent(viewEntryStateFlow.value))
                }
            }
        }
    }
}

sealed class ViewEntryEvent
data object EditClickedEvent : ViewEntryEvent()
data object DeleteClickedEvent : ViewEntryEvent()
data object DeleteConfirmedEvent : ViewEntryEvent()
data object DeleteRejectedEvent : ViewEntryEvent()

sealed class ViewEntryUIEvent
data object NavigateBackEvent : ViewEntryUIEvent()
data class NavigateToEditEvent(val state: ViewEntryState) : ViewEntryUIEvent()