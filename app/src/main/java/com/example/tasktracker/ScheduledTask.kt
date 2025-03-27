package com.example.tasktracker
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity()
data class ScheduledTask(
    val title: String,
    val description: String,
    val dueTime: String,
    val creationTime: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
