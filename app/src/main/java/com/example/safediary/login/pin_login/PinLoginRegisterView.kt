package com.example.safediary.login.pin_login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.safediary.R

@Composable
fun PinLoginView(state: PinLoginState, onEvent: (PinLoginEvent) -> Unit) {

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.size(48.dp))
            Text(
                text = stringResource(id = R.string.login_enter_pin),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))
            OtpTextField(
                otpText = state.pin,
                onOtpTextChange = { text, isFull ->
                    onEvent(InputPinEvent(text, isFull))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
            Spacer(modifier = Modifier.size(32.dp))
            Button(
                onClick = {
                    onEvent(FaceLoginClickedEvent)
                },
                enabled = !state.isLoading,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.login_log_in_via_face),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    onEvent(LoginClickedEvent)
                },
                enabled = state.isLoginEnabled && !state.isLoading,
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.login_log_in).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}