package com.example.safediary.diary.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.safediary.Constants
import com.example.safediary.DiaryLightGray
import com.example.safediary.DiaryPrimaryLight
import com.example.safediary.R
import java.time.format.DateTimeFormatter

private const val MAX_COLUMNS = 2

@Composable
fun EntriesListView(state: EntriesState, onEvent: (EntriesListEvent) -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = stringResource(id = R.string.list_your_entries),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.size(16.dp))
            OutlinedTextField(
                value = state.searchText,
                onValueChange = {
                    onEvent(FilterChangedEvent(it))
                },
                readOnly = state.isLoading,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                placeholder = {
                    Text(text = stringResource(id = R.string.list_search_by_title))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DiaryLightGray,
                    unfocusedContainerColor = DiaryLightGray,
                    focusedBorderColor = DiaryLightGray,
                    unfocusedBorderColor = DiaryLightGray
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(32.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(MAX_COLUMNS),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = {
                    items(state.entries) {
                        EntryItem(entry = it, onEvent)
                    }
                })
        }
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryItem(entry: Entry, onEvent: (EntriesListEvent) -> Unit) {
    Column {
        ElevatedCard(
            onClick = {
                onEvent(EntryClickedEvent(entry))
            },
            colors = CardDefaults.elevatedCardColors(containerColor = DiaryPrimaryLight),
            modifier = Modifier.height(200.dp)
        ) {
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp, start = 4.dp, end = 4.dp)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = entry.title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = entry.date.format(DateTimeFormatter.ofPattern(Constants.DATE_PATTERN)),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}