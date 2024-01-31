package com.example.safediary.diary.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class EntriesListViewModel(private val appService: AppService) : ViewModel() {

    private val _listState = MutableStateFlow(EntriesState())
    val listStateFlow = _listState.asStateFlow()

    private val _listEntriesChannel = Channel<EntriesListUIEvent>()
    val listEntriesChannelFlow = _listEntriesChannel.receiveAsFlow()

    private var entries: List<Entry> = emptyList()

    fun onEvent(event: EntriesListEvent) {
        when (event) {
            AddClickedEvent -> {
                viewModelScope.launch {
                    _listEntriesChannel.send(NavigateToCreationUIEvent)
                }
            }

            is EntryClickedEvent -> {
                viewModelScope.launch {
                    _listEntriesChannel.send(NavigateToDetailsUIEvent(event.id))
                }
            }

            is FilterChangedEvent -> {
                _listState.update { state ->
                    state.copy(
                        entries = entries.filter { it.title.contains(event.content, true) },
                        searchText = event.content
                    )
                }
            }

            GetEntriesEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _listState.update { state ->
                        state.copy(isLoading = true)
                    }
                    try {
                        val result = appService.getEntries().toBodyOrError<GetEntriesResult>()
                        entries = result.list.map {
                            Entry(
                                it.id,
                                it.title,
                                LocalDate.parse(
                                    it.creationDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                ),
                                it.content
                            )
                        }.sortedByDescending { it.date }
                        _listState.update { state ->
                            state.copy(entries = entries, isLoading = false)
                        }
                    } catch (e: Exception) {
                        _listEntriesChannel.send(ShowSomethingWentWrongListUIEvent)
                        _listState.update { state ->
                            state.copy(isLoading = false)
                        }
                    }
                }
            }

            FaceClickedEvent -> {
                viewModelScope.launch {
                    _listEntriesChannel.send(NavigateToFaceRegisterUIEvent)
                }
            }
        }
    }

}

sealed class EntriesListEvent
data class FilterChangedEvent(val content: String) : EntriesListEvent()
data object AddClickedEvent : EntriesListEvent()
data class EntryClickedEvent(val id: Int) : EntriesListEvent()
data object GetEntriesEvent : EntriesListEvent()
data object FaceClickedEvent : EntriesListEvent()

sealed class EntriesListUIEvent
data object NavigateToCreationUIEvent : EntriesListUIEvent()
data class NavigateToDetailsUIEvent(val id: Int) : EntriesListUIEvent()
data object NavigateToFaceRegisterUIEvent : EntriesListUIEvent()
data object ShowSomethingWentWrongListUIEvent : EntriesListUIEvent()