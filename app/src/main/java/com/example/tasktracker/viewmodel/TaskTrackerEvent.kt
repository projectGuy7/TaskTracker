package com.example.tasktracker.viewmodel

sealed interface TaskTrackerEvent {
    data class SetSearchBarContent(val searchBarContent: String): TaskTrackerEvent
    object SaveScheduledTask : TaskTrackerEvent
    data class SetTitle(val title: String): TaskTrackerEvent
    data class SetDescription(val description: String): TaskTrackerEvent
    data class SetTime(val dateTime: String): TaskTrackerEvent
    object ShowAddingScheduledTaskDialog: TaskTrackerEvent
    object HideAddingScheduledTaskDialog: TaskTrackerEvent
    data class DeleteScheduledTask(val scheduledTaskId: Int): TaskTrackerEvent
    object PressEditMode: TaskTrackerEvent
}