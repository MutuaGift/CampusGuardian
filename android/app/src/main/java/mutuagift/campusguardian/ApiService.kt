package com.mutuagift.campusguardian

import com.example.campusguardian.BackendResponse
import com.mutuagift.BackendResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    // Now this definitely uses Retrofit's Call, not the phone system
    @GET("/")
    fun checkHealth(): Call<BackendResponse>
}