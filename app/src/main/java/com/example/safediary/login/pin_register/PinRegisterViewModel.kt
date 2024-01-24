package com.example.safediary.login.pin_register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safediary.HttpRequestException
import com.example.safediary.SharedPreferencesHelper
import com.example.safediary.login.pin_login.PinLoginState
import com.example.safediary.network.AppService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class PinRegisterViewModel(private val appService: AppService, private val sharedPreferencesHelper: SharedPreferencesHelper): ViewModel() {

    private val _pinState = MutableStateFlow(PinLoginState())
    val pinState = _pinState.asStateFlow()

    private val pinChannel = Channel<PinRegisterUIEvent>()
    val pinChannelFlow = pinChannel.receiveAsFlow()

    fun onEvent(event: PinRegisterEvent) {
        when (event) {
            ContinueClickedEvent -> {
                viewModelScope.launch {
                    _pinState.update { state ->
                        state.copy(isLoading = true)
                    }
                    sharedPreferencesHelper.appId = UUID.randomUUID().toString()
                    delay(2000)
                    try {
                        //throw HttpRequestException(500, "")
                        pinChannel.send(SuccessfulRegisterEvent)
                    } catch (e: HttpRequestException) {
                        pinChannel.send(UnsuccessfulRegisterEvent)
                    }
                    _pinState.update { state ->
                        state.copy(isLoading = false)
                    }
                }
            }
            is InputPinEvent -> {
                _pinState.update { state ->
                    state.copy(pin = event.pin, isLoginEnabled = event.isFullInput)
                }
            }
        }
    }

}

sealed class PinRegisterEvent
data class InputPinEvent(val pin: String, val isFullInput: Boolean): PinRegisterEvent()
data object ContinueClickedEvent: PinRegisterEvent()

sealed class PinRegisterUIEvent
data object SuccessfulRegisterEvent: PinRegisterUIEvent()
data object UnsuccessfulRegisterEvent: PinRegisterUIEvent()
