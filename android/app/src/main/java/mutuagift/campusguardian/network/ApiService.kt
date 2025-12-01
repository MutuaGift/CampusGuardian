package mutuagift.campusguardian.network

import com.mutuagift.campusguardian.BackendResponse
import com.mutuagift.campusguardian.Ride
import com.mutuagift.campusguardian.RideRequest
import com.mutuagift.campusguardian.RideResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
    // 4. Accept a Ride (UPDATE)
    // We reuse 'BackendResponse' because we just expect a status/message back.
    @PUT("/accept-ride/{id}")
    fun acceptRide(@Path("id") rideId: String): Call<BackendResponse>
}