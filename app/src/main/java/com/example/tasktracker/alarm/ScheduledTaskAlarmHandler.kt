package com.example.tasktracker.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.core.app.AlarmManagerCompat
import com.example.tasktracker.roomdatabase.ScheduledTask
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject

class ScheduledTaskAlarmHandler @Inject constructor(private val context: Context) {
    private fun getAlarmManager(): AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    private fun permissionGranted() = getAlarmManager()?.canScheduleExactAlarms()?:false

    fun setExactAlarm(scheduledTask: ScheduledTask, type: Int = AlarmManager.RTC_WAKEUP) {
        val alarmIntent = Intent(context, ScheduledTaskAlarmReceiver::class.java).apply {
            action = ALARM_ACTION
            putExtra("Title", scheduledTask.title)
            putExtra("Description", scheduledTask.description)
            putExtra("Time", scheduledTask.time.toString())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            scheduledTask.id,
            alarmIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE, // No FLAG_MUTABLE needed for older versions
        )

        val manager = getAlarmManager()
        manager?.let {
            if(permissionGranted()) {
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    it,
                    type,
                    ZonedDateTime.now().toLocalDate().atTime(scheduledTask.time).toInstant(ZonedDateTime.now().offset).toEpochMilli(),
                    //scheduledTask.time.toEpochSecond(LocalDate.now(), ZonedDateTime.now().offset),
                    pendingIntent
                )
            }
        }
    }
}