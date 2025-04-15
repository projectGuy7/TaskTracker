package com.example.tasktracker.roomdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ScheduledTask::class], version = 1)
@TypeConverters(Converters::class)
abstract class ScheduledTaskDatabase : RoomDatabase() {
    abstract fun scheduledTaskDao(): ScheduledTaskDao
}