package com.mutuagift.campusguardian.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationHelper(private val context: Context) {

    // The entry point to Google's Location Services
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission") // We handle permission checks in the UI before calling this
    fun getCurrentLocation(onLocationFound: (Double, Double) -> Unit) {
        // Use high accuracy (GPS) to get the exact spot
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Send back the numbers (Latitude, Longitude)
                onLocationFound(location.latitude, location.longitude)
            } else {
                // If GPS is off or returns null, return 0.0 (Null Island)
                onLocationFound(0.0, 0.0)
            }
        }
    }
}