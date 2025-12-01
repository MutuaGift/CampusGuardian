package com.mutuagift.campusguardian

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // 1. Health Check (Existing)
    @GET("/")
    fun checkHealth(): Call<BackendResponse>

    // 2. NEW: Request a Ride
    // We send a RideRequest, and we expect a RideResponse
    @POST("/request-ride")
    fun requestRide(@Body request: RideRequest): Call<RideResponse>
    // 3. Driver View: Get all open rides
    @GET("/active-rides")
    fun getOpenRides(): Call<List<Ride>>
}