package com.emill.workplacetracking.utils

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.emill.workplacetracking.R
import com.emill.workplacetracking.uiViews.LeavingAreaPopUp
import java.util.concurrent.TimeUnit

class ForegroundService: Service()  {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }
    @SuppressLint("ForegroundServiceType")
    private fun start(){
        val notification = NotificationCompat.Builder(this, "foreground_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Foreground is Active")
            .setContentText("The app is running in the background.")
            .build()
        startForeground(1, notification)

        // Create a PeriodicWorkRequest for LocationCheckWorker and repeat every 15 minutes
        val locationCheckRequest = PeriodicWorkRequestBuilder<LocationCheckWorker>(15, TimeUnit.MINUTES).build()
        // Get an instance of WorkManager
        val workManager = WorkManager.getInstance(this)
        // Enqueue the request
        workManager.enqueue(locationCheckRequest)
    }
    enum class Actions{
        START, STOP
    }
}