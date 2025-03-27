package com.example.tasktracker

data class TaskTracerState (
    val scheduledTasksList : List<ScheduledTask> = emptyList(),
    val searchBarContent: String = "",
    val addingScheduledTask : Boolean = false,
    val scheduledTaskTitle: String = "",
    val scheduledTaskDescription: String = "",
    val scheduledTaskDateTime: String = "",
)