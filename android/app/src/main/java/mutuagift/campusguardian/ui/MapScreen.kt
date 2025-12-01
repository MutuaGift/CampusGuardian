package com.mutuagift.campusguardian

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen() {
    // 1. Define a starting point (e.g., Meru University coordinates)
    // Lat: 0.0470, Long: 37.6498 (Approximate)
    val meruUniversity = LatLng(0.0470, 37.6498)

    // 2. Setup the Camera
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(meruUniversity, 15f)
    }

    // 3. Draw the Map
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Add a Red Marker
        Marker(
            state = MarkerState(position = meruUniversity),
            title = "Start Here",
            snippet = "Campus Guardian HQ"
        )
    }
}