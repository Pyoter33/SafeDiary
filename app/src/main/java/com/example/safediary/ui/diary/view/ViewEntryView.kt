package com.example.safediary.ui.diary.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.safediary.R
import com.example.safediary.utils.Constants
import java.time.format.DateTimeFormatter

@Composable
fun ViewEntryView(state: ViewEntryState, onEvent: (ViewEntryEvent) -> Unit) {

    if (state.deleteDialogVisible) {
        DeleteDialog(onEvent = onEvent)
    }

    Box {
        Column(
            Modifier.padding(horizontal = 16.dp).fillMaxSize()
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = stringResource(id = R.string.create_edit_title),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.size(4.dp))
            OutlinedTextField(
                value = state.title,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = stringResource(id = R.string.create_edit_date),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.size(4.dp))
            OutlinedTextField(
                value = state.date.format(DateTimeFormatter.ofPattern(Constants.DATE_PATTERN)),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = stringResource(id = R.string.create_edit_entry),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.size(4.dp))
            OutlinedTextField(
                value = state.content,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(modifier = Modifier.size(16.dp))
        }
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun DeleteDialog(onEvent: (ViewEntryEvent) -> Unit) {
    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.view_delete_dialog_title))
        },
        text = {
            Text(text = stringResource(id = R.string.view_delete_dialog_message))
        },
        onDismissRequest = {
            onEvent(DeleteRejectedEvent)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(DeleteConfirmedEvent)
                }
            ) {
                Text(text = stringResource(id = R.string.view_yes))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(DeleteRejectedEvent)
                }
            ) {
                Text(text = stringResource(id = R.string.view_no))
            }
        }
    )
} 