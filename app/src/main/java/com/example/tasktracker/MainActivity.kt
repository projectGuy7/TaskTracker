package com.example.tasktracker

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ScheduledTaskDatabase::class.java,
            "Scheduled Task database"
        ).build()
    }

    private val taskTrackerViewModel by viewModels<TaskTrackerViewModel> (
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TaskTrackerViewModel(db.scheduledTaskDao()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val scheduledTaskAlarmHandler = ScheduledTaskAlarmHandler(this)
//        val intent = Intent().apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
//        }
//        startActivity(intent)
        setContent {
            TaskTrackerTheme {
                scheduledTaskAlarmHandler.RequestPermission(Modifier)

                val state by taskTrackerViewModel.state.collectAsState()
                MyApp(state = state, onEvent = taskTrackerViewModel::onEvent)
            }
        }
    }
}

@Composable
fun MyApp(
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
                    maxLines = 1
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
                    maxLines = 3
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
                    maxLines = 1
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
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = {
                            onEvent(TaskTrackerEvent.SetSearchBarContent(it))
                        },
                        placeholder = {
                            Text("Task Title")
                        }
                    )

                }
                LazyColumn() {
                    items(state.scheduledTasksList) { scheduledTask ->
                        ScheduledTaskItem(
                            modifier = Modifier.padding(10.dp),
                            title = scheduledTask.title,
                            titleFontSize = integerResource(R.integer.titleFontSize).sp,
                            description = scheduledTask.description,
                            descriptionFontSize = integerResource(R.integer.descriptionFontSize).sp,
                            time = scheduledTask.time,
                            timeFontSize = integerResource(R.integer.scheduledTaskTimeFontSize).sp
                        )
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
fun MyAppPreview() {
    MyApp(
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