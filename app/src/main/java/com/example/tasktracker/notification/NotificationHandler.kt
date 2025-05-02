package com.example.tasktracker.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.tasktracker.R
import com.example.tasktracker.roomdatabase.ScheduledTask
import javax.inject.Inject
import kotlin.random.Random

class NotificationHandler @Inject constructor(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val notificationChannelID = "notification_channel_id"

    fun showTaskNotification(scheduledTask: ScheduledTask) {
        val notification = NotificationCompat.Builder(context, notificationChannelID)
            .setContentTitle(scheduledTask.title)
            .setContentText(scheduledTask.description)
            .setSmallIcon(R.drawable.notification_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }
}