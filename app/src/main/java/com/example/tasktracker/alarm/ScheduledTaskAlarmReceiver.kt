package com.example.tasktracker.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import com.example.tasktracker.notification.NotificationHandler
import com.example.tasktracker.roomdatabase.ScheduledTask
import com.example.tasktracker.viewmodel.TaskTrackerState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalTime

class ScheduledTaskAlarmReceiver(private val notificationHandler: NotificationHandler): BroadcastReceiver() {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ALARM_ACTION) {
            val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakelock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "taskTracker:alarmWakelock"
            )
            wakelock.acquire(3000)
            GlobalScope.launch {
                handleIntent(intent)
            }
        }
    }

    private suspend fun handleIntent(intent: Intent?): Unit? {
        return when (intent?.action) {
            ALARM_ACTION -> {
                notificationHandler.showTaskNotification(
                    ScheduledTask(
                        intent.getStringExtra("Title")!!,
                        intent.getStringExtra("Description")!!,
                        intent.getSerializableExtra("Time", LocalTime::class.java)!!
                    )
                )
            }
            else -> {}
        }
    }

}