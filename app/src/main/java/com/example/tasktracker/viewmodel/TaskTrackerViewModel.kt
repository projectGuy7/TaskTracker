package com.example.tasktracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.roomdatabase.ScheduledTask
import com.example.tasktracker.roomdatabase.ScheduledTaskDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TaskTrackerViewModel(val dao: ScheduledTaskDao) : ViewModel() {
    private val _searchBarContent = MutableStateFlow("")
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _scheduledTasks = _searchBarContent
        .flatMapLatest {
            dao.getScheduledTasksByTaskName(it)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(TaskTrackerState())
    val state = combine(_searchBarContent, _scheduledTasks, _state) { searchBarContent, scheduledTasks, state ->
        state.copy(
            searchBarContent = searchBarContent,
            scheduledTasksList = scheduledTasks
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), TaskTrackerState())

    fun onEvent(event: TaskTrackerEvent) {
        when(event) {
            TaskTrackerEvent.HideAddingScheduledTaskDialog -> {
                _state.update {
                    it.copy(addingScheduledTask = false)
                }
            }
            TaskTrackerEvent.ShowAddingScheduledTaskDialog -> {
                _state.update {
                    it.copy(addingScheduledTask = true)
                }
            }
            TaskTrackerEvent.SaveScheduledTask -> {
                if(_state.value.run {
                    scheduledTaskTitle.isBlank() || scheduledTaskDescription.isBlank() || scheduledTaskTime.isBlank()
                }) {
                    return;
                }

                val time: LocalTime
                try {
                    time = LocalTime.parse(
                        _state.value.scheduledTaskTime,
                        DateTimeFormatter.ofPattern("HH:mm")
                    )
                } catch(e: DateTimeParseException) {
                    return;
                }

                val scheduledTask = _state.value.run {
                    ScheduledTask(
                        title = scheduledTaskTitle,
                        description = scheduledTaskDescription,
                        time = time
                    )
                }

                viewModelScope.launch {
                    dao.insertNewScheduledTask(scheduledTask)
                }

                _state.update {
                    it.copy(
                        addingScheduledTask = false,
                        scheduledTaskTitle = "",
                        scheduledTaskDescription = "",
                        scheduledTaskTime = ""
                    )
                }
            }
            is TaskTrackerEvent.SetTime -> {
                _state.update {
                    it.copy(scheduledTaskTime = event.dateTime)
                }
            }
            is TaskTrackerEvent.SetDescription -> {
                _state.update {
                    it.copy(scheduledTaskDescription = event.description)
                }
            }
            is TaskTrackerEvent.SetTitle -> {
                _state.update {
                    it.copy(scheduledTaskTitle = event.title)
                }
            }
            is TaskTrackerEvent.SetSearchBarContent -> {
                _searchBarContent.update {
                    event.searchBarContent
                }
            }
        }
    }
}