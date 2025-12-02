package com.mutuagift.campusguardian.data

data class RideRequest(
    val student_name: String,
    val pickup_location: String,
    val pickup_lat: Double,
    val pickup_lng: Double,
    val destination: String,
    val travel_mode: String
)