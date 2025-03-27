package com.example.tasktracker

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ScheduledTask::class], version = 1)
abstract class ScheduledTaskDatabase : RoomDatabase() {
    abstract fun scheduledTaskDao(): ScheduledTaskDao
}