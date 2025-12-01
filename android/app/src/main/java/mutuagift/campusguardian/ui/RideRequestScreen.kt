package com.mutuagift.campusguardian

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mutuagift.campusguardian.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun RideRequestScreen() {
    // 1. UI STATE: Variables to hold what the user types
    var pickup by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("Enter details to find a ride") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "CampusGuardian Request")

        Spacer(modifier = Modifier.height(16.dp))

        // Input 1: Pickup
        OutlinedTextField(
            value = pickup,
            onValueChange = { pickup = it },
            label = { Text("Pickup Location (e.g. Library)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input 2: Destination
        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination (e.g. Hostel A)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // The Button
        Button(
            onClick = {
                statusMessage = "Sending Request..."
                sendRequest(pickup, destination) { result ->
                    statusMessage = result
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("FIND ESCORT")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status Text
        Text(text = statusMessage)
    }
}

// The Logic Function
fun sendRequest(pickup: String, dest: String, onResult: (String) -> Unit) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.107:8000/") // YOUR REAL IP
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(ApiService::class.java)

    // Create the data packet
    val requestData = RideRequest(
        student_name = "Gift", // Hardcoded for now
        pickup_location = pickup,
        destination = dest
    )

    // Send it
    api.requestRide(requestData).enqueue(object : Callback<RideResponse> {
        override fun onResponse(call: Call<RideResponse>, response: Response<RideResponse>) {
            if (response.isSuccessful) {
                val reply = response.body()
                onResult("Success! ${reply?.message}")
            } else {
                onResult("Error: Server rejected request")
            }
        }

        override fun onFailure(call: Call<RideResponse>, t: Throwable) {
            onResult("Failed: ${t.message}")
        }
    })
}