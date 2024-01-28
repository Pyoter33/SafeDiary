package com.example.safediary.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.safediary.AppTheme
import com.example.safediary.utils.SharedPreferencesHelper
import org.koin.android.ext.android.inject

class LoginActivity : ComponentActivity() {

    private val sharedPreferencesHelper by inject<SharedPreferencesHelper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startDestination = sharedPreferencesHelper.appId?.let { ROUTE_FACE_LOGIN } ?: ROUTE_PIN_REGISTER
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavHost(startDestination = startDestination)
                }
            }
        }
    }
}


