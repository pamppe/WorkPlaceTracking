package com.emill.workplacetracking.uiViews

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import org.osmdroid.util.GeoPoint

@Composable
fun GpsScreen(navController: NavController) {
    val context = LocalContext.current
    Log.d("Gps Screen","Gps Screen triggered")
    // Call OsmMapViewWithLocationAndArea to display the map with user location and workplace area
    OsmMapViewWithLocationAndAreaWithButton(context, workplaceGeoPoint, workplaceRadius)
}

/*
val workplaceGeoPoint = GeoPoint(60.158215, 24.879721) // Convert workplace location to GeoPoint (Emil)
*/
val workplaceGeoPoint = GeoPoint(60.218764, 24.747425) // Convert workplace location to GeoPoint (Leo)

const val workplaceRadius = 20.0 // meters
