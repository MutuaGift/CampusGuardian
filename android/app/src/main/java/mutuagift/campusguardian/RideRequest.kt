package com.mutuagift.campusguardian

data class RideRequest(
    val student_name: String,
    val pickup_location: String,
    val destination: String
)