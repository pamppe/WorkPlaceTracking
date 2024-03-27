package com.emill.workplacetracking.uiViews

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.osmdroid.util.GeoPoint

@Composable
fun GpsScreen(context: Context = LocalContext.current) {
    Log.d("Gps Screen","Gps Screen triggered")
    // Call OsmMapViewWithLocationAndArea to display the map with user location and workplace area
    OsmMapViewWithLocationAndAreaWithButton(context, workplaceGeoPoint, workplaceRadius)
}

val workplaceGeoPoint = GeoPoint(60.158215, 24.879721) // Convert workplace location to GeoPoint
const val workplaceRadius = 20.0 // meters
