package com.mutuagift.campusguardian.network

// *** CRITICAL IMPORTS ***
import com.mutuagift.campusguardian.data.BackendResponse
import com.mutuagift.campusguardian.data.Ride         // <--- Import the Ride class
import com.mutuagift.campusguardian.data.RideRequest
import com.mutuagift.campusguardian.data.RideResponse
import com.mutuagift.campusguardian.data.SOSSignal
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/")
    fun checkHealth(): Call<BackendResponse>

    @POST("/request-ride")
    fun requestRide(@Body request: RideRequest): Call<RideResponse>

    // Now it can find 'Ride' because we imported it above
    @GET("/active-rides")
    fun getOpenRides(): Call<List<Ride>>

    @PUT("/accept-ride/{id}")
    fun acceptRide(@Path("id") rideId: String): Call<BackendResponse>

    @GET("/rides/search")
    fun searchRides(@Query("destination") destination: String): Call<List<Ride>>

    // 6. SOS BUTTON
    @POST("/sos")
    fun sendSOS(@Body signal: SOSSignal): Call<BackendResponse>
}