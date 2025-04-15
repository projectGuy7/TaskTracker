package com.example.tasktracker.roomdatabase

import androidx.room.TypeConverter
import java.time.LocalTime


class Converters {
    @TypeConverter
    fun fromTimeStamp(timestamp: String?): LocalTime? {
        return timestamp?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun timeToTimeStamp(time: LocalTime?): String? {
        return time?.toString()
    }
}