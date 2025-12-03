package com.mutuagift.campusguardian.data

data class Ride(
    val _id: String,
    val student: String,
    val pickup: String,
    val destination: String,
    val status: String,
    val travel_mode: String,
    // *** ADD THESE TWO LINES ***
    val pickup_lat: Double,
    val pickup_lng: Double
)