// 1. THIS MUST BE IN THE 'data' PACKAGE
package com.mutuagift.campusguardian.data

// 2. The Data Class
data class Ride(
    val _id: String,
    val student: String,
    val pickup: String,
    val destination: String,
    val status: String
)