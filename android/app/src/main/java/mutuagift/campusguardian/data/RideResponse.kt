// 1. MUST BE IN THE 'data' PACKAGE
package com.mutuagift.campusguardian.data

// 2. The Data Class
data class RideResponse(
    val status: String,
    val ride_id: String,
    val message: String
)