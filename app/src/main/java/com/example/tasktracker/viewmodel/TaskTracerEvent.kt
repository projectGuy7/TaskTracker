package com.example.tasktracker.viewmodel

sealed interface TaskTracerEvent {
    data class SetSearchBarContent(val searchBarContent: String): TaskTracerEvent
    object SaveScheduledTask : TaskTracerEvent
    data class SetTitle(val title: String): TaskTracerEvent
    data class SetDescription(val description: String): TaskTracerEvent
    data class SetTime(val dateTime: String): TaskTracerEvent
    object ShowAddingScheduledTaskDialog : TaskTracerEvent
    object HideAddingScheduledTaskDialog : TaskTracerEvent
}