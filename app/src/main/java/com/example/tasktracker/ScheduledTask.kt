package com.example.tasktracker
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity()
data class ScheduledTask(
    val title: String,
    val description: String,
    val dateTime: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
