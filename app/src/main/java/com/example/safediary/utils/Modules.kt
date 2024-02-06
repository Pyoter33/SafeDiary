package com.example.safediary.utils

import androidx.lifecycle.SavedStateHandle
import com.example.safediary.domain.diary.DeleteEntryUseCase
import com.example.safediary.domain.diary.GetAllEntriesUseCase
import com.example.safediary.domain.diary.GetEntryUseCase
import com.example.safediary.domain.diary.PostEntryUseCase
import com.example.safediary.domain.diary.UpdateEntryUseCase
import com.example.safediary.domain.login.FaceLoginUseCase
import com.example.safediary.domain.login.FaceRegisterUseCase
import com.example.safediary.domain.login.PinLoginUseCase
import com.example.safediary.domain.login.PinRegisterUseCase
import com.example.safediary.network.AppService
import com.example.safediary.network.AuthInterceptor
import com.example.safediary.ui.diary.create_edit.CreateEditEntryViewModel
import com.example.safediary.ui.diary.create_edit.SpeechRecognizerHelper
import com.example.safediary.ui.diary.list.EntriesListViewModel
import com.example.safediary.ui.diary.view.ViewEntryViewModel
import com.example.safediary.ui.login.face_login.FaceLoginRegisterViewModel
import com.example.safediary.ui.login.pin_login.PinLoginViewModel
import com.example.safediary.ui.login.pin_register.PinRegisterViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.gson.gson
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { SharedPreferencesHelper(get()) }
    single { AppService(get()) }
    single { AuthInterceptor(get()) }
    single { SpeechRecognizerHelper(get()) }
    single {
        HttpClient(OkHttp) {
            engine {
                addInterceptor(get<AuthInterceptor>())
            }
            defaultRequest {
                url(Constants.BASE_URL)
            }
            install(HttpTimeout) {
                connectTimeoutMillis = CONNECT_TIMEOUT
                requestTimeoutMillis = REQUEST_TIMEOUT
                socketTimeoutMillis = SOCKET_TIMEOUT
            }
            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                    disableHtmlEscaping()
                }
            }
        }
    }
    viewModel { FaceLoginRegisterViewModel(get(), get(), get()) }
    viewModel { PinLoginViewModel(get(), get()) }
    viewModel { PinRegisterViewModel(get(), get()) }
    viewModel { (handle: SavedStateHandle) ->  CreateEditEntryViewModel(handle, get(), get(), get()) }
    viewModel { (handle: SavedStateHandle) -> ViewEntryViewModel(handle, get(), get()) }
    viewModel { ( EntriesListViewModel(get())) }

    single { FaceLoginUseCase(get()) }
    single { FaceRegisterUseCase(get()) }
    single { PinLoginUseCase(get()) }
    single { PinRegisterUseCase(get()) }
    single { DeleteEntryUseCase(get()) }
    single { GetAllEntriesUseCase(get()) }
    single { GetEntryUseCase(get()) }
    single { PostEntryUseCase(get()) }
    single { UpdateEntryUseCase(get()) }
}

private const val CONNECT_TIMEOUT = 20000L
private const val REQUEST_TIMEOUT = 30000L
private const val SOCKET_TIMEOUT = 30000L