package com.example.safediary.ui.login.face_login

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safediary.domain.login.FaceLoginUseCase
import com.example.safediary.domain.login.FaceRegisterUseCase
import com.example.safediary.utils.HttpRequestException
import com.example.safediary.utils.SharedPreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FaceLoginRegisterViewModel(
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val faceLoginUseCase: FaceLoginUseCase,
    private val faceRegisterUseCase: FaceRegisterUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())

    private val loginChannel = Channel<LoginUIEvent>()
    val loginChannelFlow = loginChannel.receiveAsFlow()

    private val registerChannel = Channel<RegisterUIEvent>()
    val registerChannelFlow = registerChannel.receiveAsFlow()

    fun onEvent(loginEvent: FaceLoginEvent) {
        when (loginEvent) {
            is LoginWithFaceEvent -> {
                if (_loginState.value.canScan) {
                    loginWithFace(loginEvent.bitmap)
                }
            }

            is RegisterFaceEvent -> {
                if (_loginState.value.canScan) {
                    registerWithFace(loginEvent.bitmap)
                }
            }

            WaitForPositionEvent -> {
                viewModelScope.launch {
                    delay(FACE_POSITIONING_DELAY)
                    _loginState.update { state ->
                        state.copy(canScan = true)
                    }
                }
            }

            BackClickedEvent -> {
                viewModelScope.launch {
                    loginChannel.send(BackToPinUIEvent)
                }
            }
        }
    }

    private fun loginWithFace(image: Bitmap) {
        _loginState.update { state ->
            state.copy(canScan = false, numberOfScans = state.numberOfScans + 1)
        }
        viewModelScope.launch(Dispatchers.IO) {
            val appId = sharedPreferencesHelper.appId!!
            try {
                sharedPreferencesHelper.token = faceLoginUseCase.invoke(image, appId)
                loginChannel.send(SuccessfulLoginUIEvent)
            } catch (e: HttpRequestException) {
                e.printStackTrace()
                if (_loginState.value.numberOfScans >= MAX_NUMBER_OF_LOGIN_TRIES) {
                    loginChannel.send(UnsuccessfulLoginUIEvent)
                } else {
                    _loginState.update { state ->
                        state.copy(canScan = true)
                    }
                }
            }
        }
    }

    private fun registerWithFace(image: Bitmap) {
        _loginState.update { state ->
            state.copy(canScan = false)
        }
        viewModelScope.launch(Dispatchers.IO) {
            val appId = sharedPreferencesHelper.appId!!
            try {
                faceRegisterUseCase(image, appId)
                registerChannel.send(SuccessfulRegisterUIEvent)
            } catch (e: HttpRequestException) {
                e.printStackTrace()
                registerChannel.send(UnsuccessfulRegisterUIEvent)
            }
        }
    }

    companion object {
        private const val MAX_NUMBER_OF_LOGIN_TRIES = 3
        private const val FACE_POSITIONING_DELAY = 3000L
    }
}

sealed class FaceLoginEvent
data class LoginWithFaceEvent(val bitmap: Bitmap) : FaceLoginEvent()
data class RegisterFaceEvent(val bitmap: Bitmap) : FaceLoginEvent()
data object WaitForPositionEvent : FaceLoginEvent()
data object BackClickedEvent : FaceLoginEvent()

sealed class LoginUIEvent
data object SuccessfulLoginUIEvent : LoginUIEvent()
data object UnsuccessfulLoginUIEvent : LoginUIEvent()
data object BackToPinUIEvent : LoginUIEvent()

sealed class RegisterUIEvent
data object SuccessfulRegisterUIEvent : RegisterUIEvent()
data object UnsuccessfulRegisterUIEvent : RegisterUIEvent()