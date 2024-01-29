package com.example.safediary.diary.view

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safediary.Constants
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

class ViewEntryViewModel(private val appService: AppService, savedStateHandle: SavedStateHandle) : ViewModel() {

    private val argId by lazy { savedStateHandle.get<Int>(Constants.ARG_ENTRY_ID) }

    private val _viewEntryState = MutableStateFlow(ViewEntryState())
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
                    state.copy(deleteDialogVisible = false, isLoading = true)
                }
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val id = argId ?: throw IllegalStateException()
                        appService.deleteEntry(id).toBodyOrError<Unit>()
                        _viewEntryChannel.send(NavigateBackEvent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _viewEntryChannel.send(SomethingWentWrongUIEvent)
                    }
                    _viewEntryState.update { state ->
                        state.copy(isLoading = false)
                    }
                }
            }

            DeleteRejectedEvent -> {
                _viewEntryState.update { state ->
                    state.copy(deleteDialogVisible = false)
                }
            }

            EditClickedEvent -> {
                viewModelScope.launch {
                    _viewEntryChannel.send(NavigateToEditEvent(viewEntryStateFlow.value.id))
                }
            }

            GetDataEvent -> {
                _viewEntryState.update {  state ->
                    state.copy(isLoading = true)
                }
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val id = argId ?: throw IllegalStateException()
                        val entry = appService.getEntry(id).toBodyOrError<GetEntryResult>()
                        _viewEntryState.update { state ->
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
                        _viewEntryChannel.send(SomethingWentWrongUIEvent)
                    }
                    _viewEntryState.update { state ->
                        state.copy(isLoading = false)
                    }
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
data object GetDataEvent : ViewEntryEvent()

sealed class ViewEntryUIEvent
data object NavigateBackEvent : ViewEntryUIEvent()
data class NavigateToEditEvent(val id: Int) : ViewEntryUIEvent()
data object SomethingWentWrongUIEvent : ViewEntryUIEvent()