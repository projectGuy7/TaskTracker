package com.example.tasktracker.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

open class NotificationApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val notificationChannel = NotificationChannel(
            "task_notification",
            "Task Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Setting up the channel
        notificationManager.createNotificationChannel(notificationChannel)
        println("AYO")
    }
}