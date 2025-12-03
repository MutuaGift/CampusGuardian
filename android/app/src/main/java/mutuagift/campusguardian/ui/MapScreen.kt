package com.mutuagift.campusguardian.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties // <--- New Import
import com.google.maps.android.compose.MapType       // <--- New Import
import com.google.maps.android.compose.MapUiSettings // <--- New Import
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(targetLat: Double, targetLng: Double) {

    // 1. Create the Location Point
    val rideLocation = LatLng(targetLat, targetLng)

    // 2. Setup the Camera (Zoom level 16 is good for streets)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(rideLocation, 16f)
    }

    // 3. Render the Map with "Pro" features
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,

        // VISUAL SETTINGS
        properties = MapProperties(
            isBuildingEnabled = true,      // Show 3D buildings
            isTrafficEnabled = true,       // Show traffic lines
            mapType = MapType.NORMAL       // Options: SATELLITE, HYBRID, TERRAIN
        ),

        // INTERACTION SETTINGS
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,    // Show +/- buttons
            compassEnabled = true,         // Show compass
            rotationGesturesEnabled = true // Allow spinning the map
        )
    ) {
        // 4. The Pin
        Marker(
            state = MarkerState(position = rideLocation),
            title = "Student Location",
            snippet = "Pickup Point"
        )
    }
}