package com.example.safediary.diary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor

@Composable
fun CreateEntryView() {
    val state = rememberRichTextState()
    var checkedState by remember { mutableStateOf(false)}

    checkedState = state.isOrderedList
    Column {
        FilledIconToggleButton(checked = checkedState, onCheckedChange = {
            checkedState = it
            state.toggleOrderedList()
        }) {
            Icon(imageVector = Icons.Default.List, contentDescription = null)
        }
        RichTextEditor(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )

    }


}