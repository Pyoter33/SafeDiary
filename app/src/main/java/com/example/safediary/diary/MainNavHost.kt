package com.example.safediary.diary

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safediary.DiaryPrimary
import com.example.safediary.R
import com.example.safediary.diary.create_edit.CreateEditEntryView
import com.example.safediary.diary.create_edit.CreateEditEntryViewModel
import com.example.safediary.diary.create_edit.SpeechRecognizerHelper
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private const val ROUTE_ENTRY_LIST = "EntryList"
private const val ROUTE_ENTRY_CREATE_EDIT = "EntryCreateEdit"
private const val ROUTE_ENTRY_VIEW = "EntryView"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_ENTRY_CREATE_EDIT
) {

    var showEditDeleteIcons by remember {
        mutableStateOf(false)
    }
    var showUpdateFaceIcon by remember {
        mutableStateOf(false)
    }
    var showAddButton by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.route) {
                ROUTE_ENTRY_VIEW -> {
                    showEditDeleteIcons = true
                    showUpdateFaceIcon = false
                    showAddButton = false
                }

                ROUTE_ENTRY_LIST -> {
                    showEditDeleteIcons = false
                    showUpdateFaceIcon = true
                    showAddButton = true
                }

                else -> {
                    showEditDeleteIcons = false
                    showUpdateFaceIcon = false
                    showAddButton = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DiaryPrimary),
                actions = {
                    if (showUpdateFaceIcon) {
                        IconButton(onClick = { }) {
                            Icon(imageVector = Icons.Default.Face, contentDescription = null)
                        }
                    }
                    if (showEditDeleteIcons) {
                        IconButton(onClick = { }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                        }
                        IconButton(onClick = { }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (showAddButton) {
                FloatingActionButton(
                    onClick = { },
                ) {
                    Icon(Icons.Filled.Add, null)
                }
            }
        }
    ) { values ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(values)
        ) {
            composable(ROUTE_ENTRY_CREATE_EDIT) {
                val viewModel: CreateEditEntryViewModel = koinViewModel()
                val state by viewModel.createEditEntryState.collectAsState()
                val speechRecognizerHelper = koinInject<SpeechRecognizerHelper>()
                CreateEditEntryView(state, viewModel::onEvent, speechRecognizerHelper)
            }
        }
    }
}