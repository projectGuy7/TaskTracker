package com.example.tasktracker.viewmodel

import com.example.tasktracker.roomdatabase.ScheduledTask
import java.time.LocalTime

data class TaskTracerState (
    val scheduledTasksList : List<ScheduledTask> = emptyList(),
    val searchBarContent: String = "",
    val addingScheduledTask : Boolean = false,
    val scheduledTaskTitle: String = "",
    val scheduledTaskDescription: String = "",
    val scheduledTaskTime: String = ""
)