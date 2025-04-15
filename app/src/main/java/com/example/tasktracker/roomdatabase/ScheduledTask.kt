package com.example.tasktracker.roomdatabase
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity()
data class ScheduledTask(
    val title: String,
    val description: String,
    val dueTime: LocalTime,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
