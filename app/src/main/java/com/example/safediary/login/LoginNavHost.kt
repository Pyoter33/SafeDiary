package com.example.safediary.login

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.safediary.R
import com.example.safediary.diary.MainActivity
import com.example.safediary.login.face_login.BackToPinUIEvent
import com.example.safediary.login.face_login.FaceLoginRegisterView
import com.example.safediary.login.face_login.FaceLoginRegisterViewModel
import com.example.safediary.login.face_login.SuccessfulLoginUIEvent
import com.example.safediary.login.face_login.SuccessfulRegisterUIEvent
import com.example.safediary.login.face_login.UnsuccessfulLoginUIEvent
import com.example.safediary.login.face_login.UnsuccessfulRegisterUIEvent
import com.example.safediary.login.pin_login.FaceLoginNavigation
import com.example.safediary.login.pin_login.PinLoginView
import com.example.safediary.login.pin_login.PinLoginViewModel
import com.example.safediary.login.pin_login.SuccessfulLoginEvent
import com.example.safediary.login.pin_login.UnsuccessfulLoginEvent
import com.example.safediary.login.pin_register.PinRegisterView
import com.example.safediary.login.pin_register.PinRegisterViewModel
import com.example.safediary.login.pin_register.SuccessfulRegisterEvent
import com.example.safediary.login.pin_register.UnsuccessfulRegisterEvent
import org.koin.androidx.compose.koinViewModel

const val ROUTE_FACE_LOGIN = "FaceLogin"
private const val ROUTE_FACE_REGISTER = "FaceRegister"
private const val ROUTE_PIN_LOGIN = "PinLogin"
const val ROUTE_PIN_REGISTER = "PinRegister"

private const val ARG_IS_FROM_UNSUCCESSFUL_FACE_LOGIN = "isFromUnsuccessfulFaceLogin"

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }) { values ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(values)
        ) {
            composable(ROUTE_FACE_LOGIN) {
                val lifecycleOwner = LocalLifecycleOwner.current
                val context = LocalContext.current
                val viewModel: FaceLoginRegisterViewModel = koinViewModel()

                LaunchedEffect(lifecycleOwner) {
                    viewModel.loginChannelFlow.collect { event ->
                        when (event) {
                            SuccessfulLoginUIEvent -> {
                                context.startActivity(Intent(context, MainActivity::class.java))
                            }

                            BackToPinUIEvent -> {
                                navController.navigate(ROUTE_PIN_LOGIN)
                            }

                            UnsuccessfulLoginUIEvent -> {
                                navController.navigate("$ROUTE_PIN_LOGIN?$ARG_IS_FROM_UNSUCCESSFUL_FACE_LOGIN=true")
                            }
                        }
                    }
                }
                FaceLoginRegisterView(onEvent = viewModel::onEvent)
            }

            composable(
                route = "$ROUTE_PIN_LOGIN?$ARG_IS_FROM_UNSUCCESSFUL_FACE_LOGIN={$ARG_IS_FROM_UNSUCCESSFUL_FACE_LOGIN}",
                arguments = listOf(
                    navArgument(ARG_IS_FROM_UNSUCCESSFUL_FACE_LOGIN) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )) { backStackEntry ->
                val lifecycleOwner = LocalLifecycleOwner.current
                val context = LocalContext.current
                val viewModel: PinLoginViewModel = koinViewModel()
                val incorrectPinText = stringResource(
                    id = R.string.login_incorrect_pin
                )
                val unsuccessfulFaceLoginText = stringResource(
                    id = R.string.login_unsuccessful_face_login
                )
                val pinLoginState by viewModel.pinState.collectAsState()

                LaunchedEffect(Unit) {
                    if (backStackEntry.arguments?.getBoolean(ARG_IS_FROM_UNSUCCESSFUL_FACE_LOGIN) == true) {
                        showSnackbar(snackbarHostState, unsuccessfulFaceLoginText)
                    }
                }

                LaunchedEffect(lifecycleOwner) {
                    viewModel.pinChannelFlow.collect { event ->
                        when (event) {
                            SuccessfulLoginEvent -> {
                                context.startActivity(Intent(context, MainActivity::class.java))
                            }

                            UnsuccessfulLoginEvent -> {
                                showSnackbar(snackbarHostState, incorrectPinText)
                            }

                            FaceLoginNavigation -> {
                                navController.navigate(ROUTE_FACE_LOGIN)
                            }
                        }
                    }
                }
                PinLoginView(state = pinLoginState, viewModel::onEvent)
            }
            composable(ROUTE_PIN_REGISTER) {
                val lifecycleOwner = LocalLifecycleOwner.current
                val context = LocalContext.current
                val viewModel: PinRegisterViewModel = koinViewModel()
                val pinRegisterState by viewModel.pinState.collectAsState()
                LaunchedEffect(lifecycleOwner) {
                    viewModel.pinChannelFlow.collect { event ->
                        when (event) {
                            SuccessfulRegisterEvent -> {
                                navController.navigate(ROUTE_FACE_REGISTER)
                            }
                            UnsuccessfulRegisterEvent -> {
                                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                PinRegisterView(state = pinRegisterState, viewModel::onEvent)
            }
            composable(ROUTE_FACE_REGISTER) {
                val lifecycleOwner = LocalLifecycleOwner.current
                val context = LocalContext.current
                val viewModel: FaceLoginRegisterViewModel = koinViewModel()
                LaunchedEffect(lifecycleOwner) {
                    viewModel.registerChannelFlow.collect { event ->
                        when (event) {
                            SuccessfulRegisterUIEvent -> {
                                context.startActivity(Intent(context, MainActivity::class.java))
                            }
                            UnsuccessfulRegisterUIEvent -> {
                                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
                                context.startActivity(Intent(context, MainActivity::class.java))
                            }
                        }
                    }
                }
                FaceLoginRegisterView(onEvent = viewModel::onEvent, isRegister = true)
            }
        }
    }
}


private suspend fun showSnackbar(snackbarHostState: SnackbarHostState, text: String) {
    snackbarHostState.showSnackbar(text)
}