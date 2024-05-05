package com.emill.workplacetracking.utils
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.emill.workplacetracking.MyAPI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class GPSManager(private val context: Context, private val retrofit: Retrofit) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun startLocationUpdates(locationCallback: LocationCallback) {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            // Note: This is just a placeholder. Actual permission request logic needs to be implemented.
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    fun isWithinWorkplace(currentLocation: Location, workplaceLocation: Location, radius: Float): Boolean {
        val distance = currentLocation.distanceTo(workplaceLocation)
        return distance <= radius
    }

    fun stopLocationUpdates(locationCallback: LocationCallback) {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    suspend fun updateUserStatusInArea(workerId: Int, workAreaId: Int, isActive: Boolean) {
        // Assuming you have a Retrofit instance `retrofit`
        val service = retrofit.create(MyAPI::class.java)
        try {
            val response = service.updateUserStatusInArea(workerId, workAreaId, isActive)
            if (response.isSuccessful) {
                Log.d("GPSManager", "User status updated successfully")
            } else {
                throw Exception("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.d("GPSManager", "Exception: ${e.message}")
        }
    }

    fun startMonitoringWorkArea(workerId: Int, workAreaId: Int, workplaceLocation: Location, radius: Float) {
        val locationCallback = object : LocationCallback() {
            var wasWithinWorkplace = false

            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val isWithinWorkplace = isWithinWorkplace(location, workplaceLocation, radius)
                    if (isWithinWorkplace && !wasWithinWorkplace) {
                        // The user has entered the work area
                        GlobalScope.launch {
                            updateUserStatusInArea(workerId, workAreaId, true)
                        }
                    } else if (!isWithinWorkplace && wasWithinWorkplace) {
                        // The user has left the work area
                        GlobalScope.launch {
                            updateUserStatusInArea(workerId, workAreaId, false)
                        }
                    }
                    wasWithinWorkplace = isWithinWorkplace
                }
            }
        }

        startLocationUpdates(locationCallback)
    }

}

