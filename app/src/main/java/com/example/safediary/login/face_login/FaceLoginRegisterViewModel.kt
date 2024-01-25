package com.example.safediary.login.face_login

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safediary.HttpRequestException
import com.example.safediary.SharedPreferencesHelper
import com.example.safediary.dto.FaceLoginDto
import com.example.safediary.network.AppService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import kotlin.io.encoding.ExperimentalEncodingApi


class FaceLoginRegisterViewModel(private val sharedPreferencesHelper: SharedPreferencesHelper, private val appService: AppService) : ViewModel() {

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

    @OptIn(ExperimentalEncodingApi::class)
    private fun generateBase64String(image: Bitmap): ByteArray {
        val byteBuffer = ByteBuffer.allocate(image.byteCount)
        image.copyPixelsToBuffer(byteBuffer)
        byteBuffer.rewind()
        return byteBuffer.array()
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun loginWithFace(image: Bitmap) {
        _loginState.update { state ->
            state.copy(canScan = false, numberOfScans = state.numberOfScans + 1)
        }
        viewModelScope.launch(Dispatchers.IO) {
            val base64Image = generateBase64String(image)
            val appId = sharedPreferencesHelper.appId!!
            try {
                //val result: String = throw HttpRequestException(404, "")
                loginChannel.send(SuccessfulLoginUIEvent)
            } catch (e: HttpRequestException) {
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
            val byteArray = generateBase64String(image)
            val appId = sharedPreferencesHelper.appId!!
            try {
                appService.registerWithFace(FaceLoginDto(appId, byteArray))
                registerChannel.send(SuccessfulRegisterUIEvent)
            } catch (e: HttpRequestException) {
                registerChannel.send(UnsuccessfulRegisterUIEvent)
            }
        }
    }

    companion object {
        private const val MAX_NUMBER_OF_LOGIN_TRIES = 3
        private const val FACE_POSITIONING_DELAY = 5000L
    }
}

sealed class FaceLoginEvent
data class LoginWithFaceEvent(val bitmap: Bitmap): FaceLoginEvent()
data class RegisterFaceEvent(val bitmap: Bitmap): FaceLoginEvent()
data object WaitForPositionEvent: FaceLoginEvent()
data object BackClickedEvent: FaceLoginEvent()

sealed class LoginUIEvent
data object SuccessfulLoginUIEvent: LoginUIEvent()
data object UnsuccessfulLoginUIEvent: LoginUIEvent()
data object BackToPinUIEvent: LoginUIEvent()

sealed class RegisterUIEvent
data object SuccessfulRegisterUIEvent: RegisterUIEvent()
data object UnsuccessfulRegisterUIEvent: RegisterUIEvent()