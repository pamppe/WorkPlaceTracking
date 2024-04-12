package com.emill.workplacetracking.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class LocationCheckWorker(context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {


    override fun doWork(): Result {
        // Get the FusedLocationProviderClient
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Try to get the last location
            val task = fusedLocationClient.lastLocation
            task.addOnSuccessListener { location ->
                // Get the current time
                val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                if (location != null) {
                    // check if the location is within the desired range
                    Log.d("LocationCheckWorker", "Location checked at $currentTime: $location")
                } else {
                    Log.d("LocationCheckWorker", "Location is null at $currentTime")
                }
            }
            task.addOnFailureListener {
                Log.d("LocationCheckWorker", "Failed to get location")
            }
        } else {
            Log.d("LocationCheckWorker", "Location permission not granted")
        }

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}