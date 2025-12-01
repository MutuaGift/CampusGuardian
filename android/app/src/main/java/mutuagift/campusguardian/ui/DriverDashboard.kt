package com.mutuagift.campusguardian

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mutuagift.campusguardian.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun DriverDashboard() {
    var ridesList by remember { mutableStateOf<List<Ride>>(emptyList()) }
    var statusText by remember { mutableStateOf("Loading rides...") }
    val context = LocalContext.current // To show Toast messages

    // Function to Refresh the List
    fun fetchRides() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.107:8000/") // YOUR REAL IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)

        api.getOpenRides().enqueue(object : Callback<List<Ride>> {
            override fun onResponse(call: Call<List<Ride>>, response: Response<List<Ride>>) {
                if (response.isSuccessful) {
                    ridesList = response.body() ?: emptyList()
                    statusText = "Active Rides: ${ridesList.size}"
                }
            }
            override fun onFailure(call: Call<List<Ride>>, t: Throwable) {
                statusText = "Error: ${t.message}"
            }
        })
    }

    // Function to Accept a Ride
    fun acceptRide(rideId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.107:8000/") // YOUR REAL IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)

        api.acceptRide(rideId).enqueue(object : Callback<BackendResponse> {
            override fun onResponse(call: Call<BackendResponse>, response: Response<BackendResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Ride Accepted!", Toast.LENGTH_SHORT).show()
                    fetchRides() // Refresh the list to show the new status
                }
            }
            override fun onFailure(call: Call<BackendResponse>, t: Throwable) {
                Toast.makeText(context, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Load data when screen opens
    LaunchedEffect(Unit) {
        fetchRides()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Driver Dashboard", fontWeight = FontWeight.Bold)
        Text(text = statusText, color = Color.Gray)
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ridesList) { ride ->
                RideCard(ride, onAcceptClick = { acceptRide(ride._id) })
            }
        }
    }
}

@Composable
fun RideCard(ride: Ride, onAcceptClick: () -> Unit) {
    // Change color if Accepted
    val cardColor = if (ride.status == "IN_PROGRESS") Color(0xFFE8F5E9) else Color.White
    val borderColor = if (ride.status == "IN_PROGRESS") Color.Green else Color.Gray

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = ride.student, fontWeight = FontWeight.Bold)
            Text(text = "From: ${ride.pickup} -> To: ${ride.destination}")

            Spacer(modifier = Modifier.height(8.dp))

            if (ride.status == "PENDING") {
                Button(
                    onClick = onAcceptClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ACCEPT RIDE")
                }
            } else {
                Text(text = "✅ IN PROGRESS", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            }
        }
    }
}