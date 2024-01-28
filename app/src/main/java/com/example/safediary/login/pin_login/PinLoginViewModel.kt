package com.example.safediary.login.pin_login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safediary.Constants
import com.example.safediary.dto.PinLoginDto
import com.example.safediary.network.AppService
import com.example.safediary.utils.HttpRequestException
import com.example.safediary.utils.SharedPreferencesHelper
import com.example.safediary.utils.toBodyOrError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PinLoginViewModel(private val appService: AppService, private val sharedPreferencesHelper: SharedPreferencesHelper): ViewModel() {

    private val _pinState = MutableStateFlow(PinLoginState())
    val pinState = _pinState.asStateFlow()

    private val pinChannel = Channel<PinLoginUIEvent>()
    val pinChannelFlow = pinChannel.receiveAsFlow()

    fun onEvent(event: PinLoginEvent) {
        when (event) {
            is InputPinEvent -> {
                _pinState.update { state ->
                    state.copy(pin = event.pin, isLoginEnabled = event.isFullInput)
                }
            }
            LoginClickedEvent -> {
                viewModelScope.launch {
                    _pinState.update { state ->
                        state.copy(isLoading = true)
                    }
                    val appId = sharedPreferencesHelper.appId!!
                    try {
                        val result = appService.loginWithPin(PinLoginDto(appId, pinState.value.pin))
                        result.toBodyOrError<Unit>()
                        sharedPreferencesHelper.token = result.headers[Constants.AUTHORIZATION_HEADER]
                        pinChannel.send(SuccessfulLoginEvent)
                    } catch (e: HttpRequestException) {
                        Log.e(this@PinLoginViewModel.javaClass.name, e.errorMessage)
                        pinChannel.send(UnsuccessfulLoginEvent)
                    }
                }
                _pinState.update { state ->
                    state.copy(isLoading = false)
                }

            }
            FaceLoginClickedEvent -> {
                viewModelScope.launch {
                    pinChannel.send(FaceLoginNavigation)
                }
            }
        }
    }

}

sealed class PinLoginEvent
data class InputPinEvent(val pin: String, val isFullInput: Boolean): PinLoginEvent()
data object LoginClickedEvent: PinLoginEvent()
data object FaceLoginClickedEvent: PinLoginEvent()

sealed class PinLoginUIEvent
data object SuccessfulLoginEvent: PinLoginUIEvent()
data object UnsuccessfulLoginEvent: PinLoginUIEvent()
data object FaceLoginNavigation: PinLoginUIEvent()
