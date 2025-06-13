package com.example.tasktracker

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.tasktracker.alarm.ScheduledTaskAlarmHandler
import com.example.tasktracker.composables.mainScreen.ScheduledTaskItem
import com.example.tasktracker.roomdatabase.ScheduledTask
import com.example.tasktracker.roomdatabase.ScheduledTaskDatabase
import com.example.tasktracker.viewmodel.TaskTrackerState
import com.example.tasktracker.viewmodel.TaskTrackerViewModel
import com.example.tasktracker.ui.theme.TaskTrackerTheme
import com.example.tasktracker.viewmodel.TaskTrackerEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var db: ScheduledTaskDatabase

    val taskTrackerViewModel: TaskTrackerViewModel by viewModels()

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> }

    @OptIn(ExperimentalPermissionsApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
//        requestPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
//        requestPermission.launch(android.Manifest.permission.USE_EXACT_ALARM)
//        requestPermission.launch(android.Manifest.permission.WAKE_LOCK)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskTrackerTheme {
                val postNotificationPermission = rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)
                LaunchedEffect(key1 = true) {
                    postNotificationPermission.launchPermissionRequest()
                }

                val state by taskTrackerViewModel.state.collectAsState()
                App(state = state, onEvent = taskTrackerViewModel::onEvent)
            }
        }
    }
}

@Composable
fun App(
    modifier: Modifier = Modifier,
    state: TaskTrackerState,
    onEvent: (TaskTrackerEvent) -> Unit
    ) {
    Scaffold(
        floatingActionButton = {
            if(!state.addingScheduledTask) {
                FloatingActionButton(
                    onClick = { onEvent(TaskTrackerEvent.ShowAddingScheduledTaskDialog) }
                ) {
                    Icon(Icons.Filled.Add, "Add new contact")
                }
            }
        }
    ) { padding ->
        if(state.addingScheduledTask) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    text = "New task Title",
                    fontSize = integerResource(R.integer.addingScheduledTaskFontSize).sp
                )
                TextField(
                    value = state.scheduledTaskTitle,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                        onEvent(TaskTrackerEvent.SetTitle(it))
                    },
                    singleLine = true
                )
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    text = "New task Description",
                    fontSize = integerResource(R.integer.addingScheduledTaskFontSize).sp
                )
                TextField(
                    value = state.scheduledTaskDescription,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                        onEvent(TaskTrackerEvent.SetDescription(it))
                    },
                    singleLine = true
                )
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    text = "New task Time",
                    fontSize = integerResource(R.integer.addingScheduledTaskFontSize).sp
                )
                TextField(
                    value = state.scheduledTaskTime,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                        onEvent(TaskTrackerEvent.SetTime(it))
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                    onClick = { onEvent(TaskTrackerEvent.SaveScheduledTask) }
                ) {
                    Text(
                        text = "Add Task",
                        fontSize = integerResource(R.integer.addingScheduledTaskFontSize).sp
                    )
                }
            }
        } else {
            Column(modifier = Modifier.padding(padding)) {
                Row() {
                    TextField(
                        value = state.searchBarContent,
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        onValueChange = {
                            onEvent(TaskTrackerEvent.SetSearchBarContent(it))
                        },
                        placeholder = {
                            Text("Task Title")
                        }
                    )
                    IconButton(
                        onClick = {
                            onEvent(TaskTrackerEvent.PressEditMode)
                        },
                        content = {
                            if(state.inEditMode) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Switch back")
                            } else {
                                Icon(Icons.Default.Create, "Switch to Edit Mode")
                            }
                        }
                    )
                }
                LazyColumn() {
                    items(state.scheduledTasksList) { scheduledTask ->
                        Row() {
                            ScheduledTaskItem(
                                modifier = Modifier.fillMaxWidth().padding(10.dp).weight(1f),
                                title = scheduledTask.title,
                                titleFontSize = integerResource(R.integer.titleFontSize).sp,
                                description = scheduledTask.description,
                                descriptionFontSize = integerResource(R.integer.descriptionFontSize).sp,
                                time = scheduledTask.time,
                                timeFontSize = integerResource(R.integer.scheduledTaskTimeFontSize).sp
                            )
                            if(state.inEditMode) {
                                Log.i("inEditMode", "Hit Recomposition")
                                IconButton(
                                    onClick = {
                                        onEvent(TaskTrackerEvent.DeleteScheduledTask(scheduledTask.id))
                                    },
                                    content = {
                                        Icon(Icons.Default.Delete, "Delete")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    widthDp = 412,
    heightDp = 732,
    showBackground = true
)
@Composable
fun AppPreview() {
    App(
        state =  TaskTrackerState(
            addingScheduledTask = true,
            scheduledTasksList = listOf(
                ScheduledTask(title = "1st Title", description = "1st Description Longggggggggggggggggggg", time = LocalTime.parse("12:20")),
                ScheduledTask(title = "2nd Title", description = "2nd Description", time = LocalTime.parse("13:20")) ,
                ScheduledTask(title = "3rd Title", description = "3rd Description", time = LocalTime.parse("14:20"))
            )
        ),
        onEvent = {}
    )
}