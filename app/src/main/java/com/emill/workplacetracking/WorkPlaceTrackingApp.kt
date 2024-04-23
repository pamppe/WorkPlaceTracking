package com.emill.workplacetracking

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class WorkPlaceTrackingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "foreground_channel",
            "Foreground Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}