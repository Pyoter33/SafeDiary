package com.example.safediary.ui.diary

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.safediary.ui.DiaryAccent
import com.example.safediary.ui.DiaryPrimary
import com.example.safediary.ui.diary.create_edit.CreateEditEntryView
import com.example.safediary.ui.diary.create_edit.CreateEditEntryViewModel
import com.example.safediary.ui.diary.create_edit.DataNotFilledUIEvent
import com.example.safediary.ui.diary.create_edit.EntryAddedUIEvent
import com.example.safediary.ui.diary.create_edit.GetDataCreateEvent
import com.example.safediary.ui.diary.create_edit.SomethingWentWrongCreateUIEvent
import com.example.safediary.ui.diary.create_edit.SpeechRecognizerHelper
import com.example.safediary.ui.diary.list.AddClickedEvent
import com.example.safediary.ui.diary.list.EntriesListView
import com.example.safediary.ui.diary.list.EntriesListViewModel
import com.example.safediary.ui.diary.list.FaceClickedEvent
import com.example.safediary.ui.diary.list.GetEntriesEvent
import com.example.safediary.ui.diary.list.NavigateToCreationUIEvent
import com.example.safediary.ui.diary.list.NavigateToDetailsUIEvent
import com.example.safediary.ui.diary.list.NavigateToFaceRegisterUIEvent
import com.example.safediary.ui.diary.list.ShowSomethingWentWrongListUIEvent
import com.example.safediary.ui.diary.view.DeleteClickedEvent
import com.example.safediary.ui.diary.view.EditClickedEvent
import com.example.safediary.ui.diary.view.GetDataEvent
import com.example.safediary.ui.diary.view.NavigateBackEvent
import com.example.safediary.ui.diary.view.NavigateToEditEvent
import com.example.safediary.ui.diary.view.SomethingWentWrongUIEvent
import com.example.safediary.ui.diary.view.ViewEntryView
import com.example.safediary.ui.diary.view.ViewEntryViewModel
import com.example.safediary.ui.login.ROUTE_FACE_REGISTER
import com.example.safediary.ui.login.face_login.FaceLoginRegisterView
import com.example.safediary.ui.login.face_login.FaceLoginRegisterViewModel
import com.example.safediary.ui.login.face_login.SuccessfulRegisterUIEvent
import com.example.safediary.ui.login.face_login.UnsuccessfulRegisterUIEvent
import com.example.safediary.utils.Constants.ARG_ENTRY_ID
import com.example.safediary.utils.Constants.DEFAULT_INT_ARG
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private const val ROUTE_ENTRY_LIST = "EntryList"
private const val ROUTE_ENTRY_CREATE_EDIT = "EntryCreateEdit"
private const val ROUTE_ENTRY_VIEW = "EntryView"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_ENTRY_LIST
) {

    var editButtonEventListener: (() -> Unit)? = null
    var deleteButtonEventListener: (() -> Unit)? = null
    var createButtonEventListener: (() -> Unit)? = null
    var faceButtonEventListener: (() -> Unit)? = null

    var showEditDeleteIcons by remember {
        mutableStateOf(false)
    }
    var showUpdateFaceIcon by remember {
        mutableStateOf(false)
    }
    var showAddButton by remember {
        mutableStateOf(false)
    }
    var showBackButton by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            showBackButton = navController.previousBackStackEntry != null
            if (destination.route == null) return@addOnDestinationChangedListener
            with (destination.route!!) {
                when {
                    contains(ROUTE_ENTRY_VIEW) -> {
                        showEditDeleteIcons = true
                        showUpdateFaceIcon = false
                        showAddButton = false
                    }

                    contains(ROUTE_ENTRY_LIST) -> {
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
    }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
        },
            navigationIcon = {
                if (showBackButton) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DiaryPrimary),
            actions = {
                if (showUpdateFaceIcon) {
                    IconButton(onClick = {
                        faceButtonEventListener?.invoke()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }
                if (showEditDeleteIcons) {
                    IconButton(onClick = {
                        editButtonEventListener?.invoke()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {
                        deleteButtonEventListener?.invoke()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }
            })
    }, floatingActionButton = {
        if (showAddButton) {
            FloatingActionButton(
                onClick = {
                    createButtonEventListener?.invoke()
                },
                containerColor = DiaryAccent,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
            }
        }
    }) { values ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(values)
        ) {
            composable(
                route = "$ROUTE_ENTRY_CREATE_EDIT?$ARG_ENTRY_ID={$ARG_ENTRY_ID}",
                arguments = listOf(
                    navArgument(ARG_ENTRY_ID) {
                        type = NavType.IntType
                        defaultValue = DEFAULT_INT_ARG
                    }
                )
            ) {
                val lifecycleOwner = LocalLifecycleOwner.current
                val context = LocalContext.current
                val viewModel: CreateEditEntryViewModel = koinViewModel()
                val state by viewModel.createEditEntryState.collectAsState()
                val speechRecognizerHelper = koinInject<SpeechRecognizerHelper>()

                LaunchedEffect(lifecycleOwner) {
                    if (it.arguments?.getInt(ARG_ENTRY_ID) != DEFAULT_INT_ARG) viewModel.onEvent(
                        GetDataCreateEvent
                    )
                    viewModel.createEditEntryChannelFlow.collect { event ->
                        when (event) {
                            DataNotFilledUIEvent -> {
                                Toast.makeText(context, R.string.create_fields_not_filled, Toast.LENGTH_SHORT).show()
                            }
                            EntryAddedUIEvent -> {
                                navController.popBackStack(ROUTE_ENTRY_LIST, false)
                            }

                            SomethingWentWrongCreateUIEvent -> {
                                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                CreateEditEntryView(state, viewModel::onEvent, speechRecognizerHelper)
            }

            composable(
                route = "$ROUTE_ENTRY_VIEW/{$ARG_ENTRY_ID}",
                arguments = listOf(
                    navArgument(ARG_ENTRY_ID) {
                        type = NavType.IntType
                    }
                )
            ) {
                val context = LocalContext.current
                val lifecycleOwner = LocalLifecycleOwner.current
                val viewModel: ViewEntryViewModel = koinViewModel()
                val state by viewModel.viewEntryStateFlow.collectAsState()
                editButtonEventListener = { viewModel.onEvent(EditClickedEvent) }
                deleteButtonEventListener = { viewModel.onEvent(DeleteClickedEvent) }

                LaunchedEffect(lifecycleOwner) {
                    viewModel.onEvent(GetDataEvent)
                    viewModel.viewEntryChannelFlow.collect { event ->
                        when (event) {
                            NavigateBackEvent -> {
                                navController.popBackStack()
                            }

                            is NavigateToEditEvent -> {
                                navController.navigate("$ROUTE_ENTRY_CREATE_EDIT?$ARG_ENTRY_ID=${event.id}")
                            }

                            SomethingWentWrongUIEvent -> {
                                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                ViewEntryView(state, viewModel::onEvent)
            }

            composable(ROUTE_ENTRY_LIST) {
                val context = LocalContext.current
                val lifecycleOwner = LocalLifecycleOwner.current
                val viewModel: EntriesListViewModel = koinViewModel()
                val state by viewModel.listStateFlow.collectAsState()
                createButtonEventListener = {
                    viewModel.onEvent(AddClickedEvent)
                }
                faceButtonEventListener = {
                    viewModel.onEvent(FaceClickedEvent)
                }

                LaunchedEffect(lifecycleOwner) {
                    viewModel.onEvent(GetEntriesEvent)
                    viewModel.listEntriesChannelFlow.collect { event ->
                        when (event) {
                            NavigateToCreationUIEvent -> {
                                navController.navigate(ROUTE_ENTRY_CREATE_EDIT)
                            }
                            is NavigateToDetailsUIEvent -> {
                                navController.navigate("$ROUTE_ENTRY_VIEW/${event.id}")
                            }

                            NavigateToFaceRegisterUIEvent -> {
                                navController.navigate(ROUTE_FACE_REGISTER)
                            }

                            ShowSomethingWentWrongListUIEvent -> {
                                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                EntriesListView(state, viewModel::onEvent)
            }

            composable(ROUTE_FACE_REGISTER) {
                val lifecycleOwner = LocalLifecycleOwner.current
                val context = LocalContext.current
                val viewModel: FaceLoginRegisterViewModel = koinViewModel()
                LaunchedEffect(lifecycleOwner) {
                    viewModel.registerChannelFlow.collect { event ->
                        when (event) {
                            SuccessfulRegisterUIEvent -> {
                                Toast.makeText(context, R.string.register_face_success, Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                            UnsuccessfulRegisterUIEvent -> {
                                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    }
                }
                FaceLoginRegisterView(onEvent = viewModel::onEvent, isRegister = true)
            }
        }
    }
}