package com.mutuagift.campusguardian

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun DriverDashboard() {
    // State to hold the list of rides
    var ridesList by remember { mutableStateOf<List<Ride>>(emptyList()) }
    var statusText by remember { mutableStateOf("Loading rides...") }

    // Fetch data when screen opens
    LaunchedEffect(Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.107:8000/") // YOUR REAL IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)

        api.getOpenRides().enqueue(object : Callback<List<Ride>> {
            override fun onResponse(call: Call<List<Ride>>, response: Response<List<Ride>>) {
                if (response.isSuccessful) {
                    ridesList = response.body() ?: emptyList()
                    statusText = "Found ${ridesList.size} active rides"
                }
            }
            override fun onFailure(call: Call<List<Ride>>, t: Throwable) {
                statusText = "Error: ${t.message}"
            }
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Driver Dashboard", fontWeight = FontWeight.Bold)
        Text(text = statusText, color = Color.Gray)

        Spacer(modifier = Modifier.height(10.dp))

        // THE SCROLLING LIST
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ridesList) { ride ->
                RideCard(ride)
            }
        }
    }
}

@Composable
fun RideCard(ride: Ride) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = ride.student, fontWeight = FontWeight.Bold)
            Text(text = "From: ${ride.pickup}")
            Text(text = "To: ${ride.destination}")
            Text(text = "Status: ${ride.status}", color = Color.Blue)
        }
    }
}