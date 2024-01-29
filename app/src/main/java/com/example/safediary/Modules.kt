package com.example.safediary

import androidx.lifecycle.SavedStateHandle
import com.example.safediary.diary.create_edit.CreateEditEntryViewModel
import com.example.safediary.diary.create_edit.SpeechRecognizerHelper
import com.example.safediary.diary.list.EntriesListViewModel
import com.example.safediary.diary.view.ViewEntryViewModel
import com.example.safediary.login.face_login.FaceLoginRegisterViewModel
import com.example.safediary.login.pin_login.PinLoginViewModel
import com.example.safediary.login.pin_register.PinRegisterViewModel
import com.example.safediary.network.AppService
import com.example.safediary.network.AuthInterceptor
import com.example.safediary.utils.SharedPreferencesHelper
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
                }
            }
        }
    }
    viewModel { FaceLoginRegisterViewModel(get(), get()) }
    viewModel { PinLoginViewModel(get(), get()) }
    viewModel { PinRegisterViewModel(get(), get()) }
    viewModel { (handle: SavedStateHandle) ->  CreateEditEntryViewModel(get(), handle) }
    viewModel { (handle: SavedStateHandle) -> ViewEntryViewModel(get(), handle) }
    viewModel { ( EntriesListViewModel(get())) }
}

private const val CONNECT_TIMEOUT = 20000L
private const val REQUEST_TIMEOUT = 30000L
private const val SOCKET_TIMEOUT = 30000L