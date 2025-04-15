package com.example.tasktracker

import android.os.Bundle
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
import com.example.tasktracker.composables.mainScreen.ScheduledTaskItem
import com.example.tasktracker.roomdatabase.ScheduledTask
import com.example.tasktracker.roomdatabase.ScheduledTaskDatabase
import com.example.tasktracker.viewmodel.TaskTracerState
import com.example.tasktracker.viewmodel.TaskTracerViewModel
import com.example.tasktracker.ui.theme.TaskTrackerTheme
import com.example.tasktracker.viewmodel.TaskTracerEvent
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

    private val taskTracerViewModel by viewModels<TaskTracerViewModel> (
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TaskTracerViewModel(db.scheduledTaskDao()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskTrackerTheme {
                val state by taskTracerViewModel.state.collectAsState()
                MyApp(state = state, onEvent = taskTracerViewModel::onEvent)
            }
        }
    }
}

@Composable
fun MyApp(
    modifier: Modifier = Modifier,
    state: TaskTracerState,
    onEvent: (TaskTracerEvent) -> Unit
    ) {
    Scaffold(
        floatingActionButton = {
            if(!state.addingScheduledTask) {
                FloatingActionButton(
                    onClick = { onEvent(TaskTracerEvent.ShowAddingScheduledTaskDialog) }
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
                        onEvent(TaskTracerEvent.SetTitle(it))
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
                        onEvent(TaskTracerEvent.SetDescription(it))
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
                        onEvent(TaskTracerEvent.SetTime(it))
                    },
                    maxLines = 1
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                    onClick = { onEvent(TaskTracerEvent.SaveScheduledTask) }
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
                            onEvent(TaskTracerEvent.SetSearchBarContent(it))
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
                            time = scheduledTask.dueTime,
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
        state =  TaskTracerState(
            addingScheduledTask = true,
            scheduledTasksList = listOf(
                ScheduledTask(title = "1st Title", description = "1st Description Longggggggggggggggggggg", dueTime = LocalTime.parse("12:20")),
                ScheduledTask(title = "2nd Title", description = "2nd Description", dueTime = LocalTime.parse("13:20")) ,
                ScheduledTask(title = "3rd Title", description = "3rd Description", dueTime = LocalTime.parse("14:20"))
            )
        ),
        onEvent = {}
    )
}