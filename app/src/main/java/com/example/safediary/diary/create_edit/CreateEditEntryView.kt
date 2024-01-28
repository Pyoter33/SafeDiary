package com.example.safediary.diary.create_edit

import android.Manifest
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.safediary.DiaryAccent
import com.example.safediary.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CreateEditEntryView(
    state: CreateEditEntryState,
    onEvent: (CreateEditEntryEvent) -> Unit,
    speechRecognizerHelper: SpeechRecognizerHelper
) {

    val permissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(Unit) {
        speechRecognizerHelper.createSpeechRecognizer()
        speechRecognizerHelper.setActionListeners(
            onSpeechResult = {
                onEvent(ContentChangedBySpeechEvent(it))
            },
            onSpeechEnd = {
                onEvent(VoiceListeningChanged(false))
            }
        )
    }

    Box {
        Column {
            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            ) {
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = stringResource(id = R.string.create_edit_title),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.size(4.dp))
                OutlinedTextField(
                    value = state.title,
                    onValueChange = {
                        onEvent(TitleChangedEvent(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()

                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = stringResource(id = R.string.create_edit_date),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.size(4.dp))
                DatePickerView(
                    text = state.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    onDateChanged = {
                        onEvent(DateChangedEvent(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = stringResource(id = R.string.create_edit_entry),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.size(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    OutlinedTextField(
                        value = state.content, onValueChange = {
                            onEvent(ContentChangedEvent(it))
                        },
                        modifier = Modifier
                            .fillMaxSize()
                    )
                    IconButton(
                        onClick = {
                            if (!permissionState.status.isGranted) {
                                permissionState.launchPermissionRequest()
                            } else {
                                speechRecognizerHelper.startListening()
                                onEvent(VoiceListeningChanged(true))
                            }
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_microphone),
                            tint = DiaryAccent,
                            contentDescription = null
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                onClick = {
                    onEvent(CreateClickedEvent)
                },
                enabled = !state.isLoading,
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.create_edit_save).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        AnimatedVisibility(
            visible = state.isListening,
            enter = fadeIn(animationSpec = tween(1000)),
            exit = fadeOut(animationSpec = tween(1000)),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_microphone),
                contentDescription = null,
                tint = DiaryAccent,
                modifier = Modifier
                    .border(BorderStroke(1.dp, DiaryAccent), shape = CircleShape)
                    .size(60.dp)
            )
        }
    }
}

@Composable
private fun DatePickerView(
    text: String,
    onDateChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            onDateChanged(LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth))
        }, year, month, dayOfMonth
    )

    OutlinedTextField(
        value = text,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier.clickable {
                    datePicker.show()
                }
            )
        },
        modifier = modifier
    )
}