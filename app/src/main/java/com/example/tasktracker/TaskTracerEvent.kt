package com.example.tasktracker

sealed interface TaskTracerEvent {
    data class setSearchBarContent(val searchBarContent: String): TaskTracerEvent
    object SaveScheduledTask : TaskTracerEvent
    data class setTitle(val title: String): TaskTracerEvent
    data class setDescription(val description: String): TaskTracerEvent
    data class setDateTime(val dateTime: String): TaskTracerEvent
    object ShowAddingScheduledTaskDialog : TaskTracerEvent
    object HideAddingScheduledTaskDialog : TaskTracerEvent
}