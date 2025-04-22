package com.example.tasktracker.viewmodel

import com.example.tasktracker.roomdatabase.ScheduledTask

data class TaskTrackerState (
    val scheduledTasksList : List<ScheduledTask> = emptyList(),
    val searchBarContent: String = "",
    val addingScheduledTask : Boolean = false,
    val scheduledTaskTitle: String = "",
    val scheduledTaskDescription: String = "",
    val scheduledTaskTime: String = ""
)