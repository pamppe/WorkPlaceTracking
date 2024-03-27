package com.emill.workplacetracking.uiViews

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun OsmMapViewWithLocationAndAreaWithButton(context: Context, workplaceLocation: GeoPoint, workplaceRadius: Double) {
    Log.d("Map", "Map Composable triggered")
    val mapView = remember { MapView(context) }
    val userLocationMarker = remember { Marker(mapView) }
    mapView.overlays.clear()
    Column {
        AndroidView(
            factory = { mapView },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            update = { mapView ->
                mapView.apply {
                    isHorizontalMapRepetitionEnabled = false
                    isVerticalMapRepetitionEnabled = false
                    minZoomLevel = 3.5

                    setTileSource(TileSourceFactory.MAPNIK)
                    controller.setCenter(workplaceLocation)
                    setMultiTouchControls(true)

                    // Add marker for workplace
                    overlays.add(Marker(mapView).apply {
                        position = workplaceLocation
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Workplace"
                        // icon = context.getDrawable(R.drawable.ic_workplace_marker) // Custom icon for workplace
                    })

                    // Add polygon for workplace radius
                    overlays.add(Polygon().apply {
                        points = Polygon.pointsAsCircle(workplaceLocation, workplaceRadius)
                        fillColor = 0x25FF0000 // Example semi-transparent fill
                    })

                    // Overlay for user's location
                    val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this).apply {
                        enableMyLocation()
                    }
                    overlays.add(myLocationOverlay)
                    // Example to update user location marker based on location updates

                }
            }
        )
        Button(
            onClick = {
                mapView.controller.setZoom(18.0)
                mapView.controller.setCenter(workplaceLocation)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Navigate to Workplace")
        }
    }
}
