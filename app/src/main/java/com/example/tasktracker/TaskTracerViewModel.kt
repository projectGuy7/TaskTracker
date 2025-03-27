package com.example.tasktracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TaskTracerViewModel(val dao: ScheduledTaskDao) : ViewModel() {
    private val _searchBarContent = MutableStateFlow("")
    private val _scheduledTasks = _searchBarContent
        .flatMapLatest {
            dao.getScheduledTasksByTaskName(it)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(TaskTracerState())
    val state = combine(_searchBarContent, _scheduledTasks, _state) { searchBarContent, scheduledTasks, state ->
        state.copy(
            searchBarContent = searchBarContent,
            scheduledTasksList = scheduledTasks
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), TaskTracerState())


    fun onEvent(event: TaskTracerEvent) {
        when(event) {
            TaskTracerEvent.HideAddingScheduledTaskDialog -> {
                _state.update {
                    it.copy(addingScheduledTask = false)
                }
            }
            TaskTracerEvent.ShowAddingScheduledTaskDialog -> {
                _state.update {
                    it.copy(addingScheduledTask = true)
                }
            }
            TaskTracerEvent.SaveScheduledTask -> {
                if(_state.value.run {
                    scheduledTaskTitle.isBlank() || scheduledTaskDescription.isBlank() || scheduledTaskDateTime.isBlank()
                }) {
                    return;
                }

                val currentTime = LocalDateTime.now()

                try {
                    val dateTime = LocalDateTime.parse(
                        _state.value.scheduledTaskDateTime,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    )
                    if(dateTime.isBefore(currentTime)) {
                        return;
                    }
                } catch(e: DateTimeParseException) {
                    return;
                    // TODO : Maybe snackbar
                }

                val scheduledTask = _state.value.run {
                    ScheduledTask(
                        title = scheduledTaskTitle,
                        description = scheduledTaskDescription,
                        dateTime = scheduledTaskDateTime
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
                        scheduledTaskDateTime = ""
                    )
                }
            }
            is TaskTracerEvent.setDateTime -> {
                _state.update {
                    it.copy(scheduledTaskDateTime = event.dateTime)
                }
            }
            is TaskTracerEvent.setDescription -> {
                _state.update {
                    it.copy(scheduledTaskDescription = event.description)
                }
            }
            is TaskTracerEvent.setTitle -> {
                _state.update {
                    it.copy(scheduledTaskTitle = event.title)
                }
            }
            is TaskTracerEvent.setSearchBarContent -> {
                _searchBarContent.update {
                    event.searchBarContent
                }
            }
        }
    }
}