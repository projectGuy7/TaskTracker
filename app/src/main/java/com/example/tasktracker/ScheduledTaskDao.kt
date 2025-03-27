package com.example.tasktracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao()
interface ScheduledTaskDao {

    @Query("SELECT * FROM scheduledtask")
    fun getAllScheduledTasks() : Flow<List<ScheduledTask>>

    @Query("SELECT * FROM scheduledtask WHERE title LIKE :title || '%'")
    fun getScheduledTasksByTaskName(title: String) : Flow<List<ScheduledTask>>
    // TODO: Possible errors in future

    @Insert
    suspend fun insertNewScheduledTask(scheduledTask: ScheduledTask)
}