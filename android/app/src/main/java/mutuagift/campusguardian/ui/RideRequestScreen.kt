package com.mutuagift.campusguardian.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

import com.mutuagift.campusguardian.data.RideRequest
import com.mutuagift.campusguardian.data.RideResponse
import com.mutuagift.campusguardian.data.SOSSignal // Import SOS
import com.mutuagift.campusguardian.data.BackendResponse
import com.mutuagift.campusguardian.network.ApiService
import com.mutuagift.campusguardian.utils.LocationHelper

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideRequestScreen() {
    // Variables
    var pickupText by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var currentLat by remember { mutableDoubleStateOf(0.0) }
    var currentLng by remember { mutableDoubleStateOf(0.0) }
    var statusMessage by remember { mutableStateOf("Select Mode & Destination") }
    var selectedMode by remember { mutableStateOf("CAR") }

    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    // --- SOS LOGIC ---
    fun triggerSOS() {
        if (currentLat == 0.0) {
            Toast.makeText(context, "Get GPS Location first!", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.107:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)

        val signal = SOSSignal(currentLat, currentLng)

        api.sendSOS(signal).enqueue(object : Callback<BackendResponse> {
            override fun onResponse(call: Call<BackendResponse>, response: Response<BackendResponse>) {
                Toast.makeText(context, "🚨 SOS SENT! HELP IS COMING! 🚨", Toast.LENGTH_LONG).show()
            }
            override fun onFailure(call: Call<BackendResponse>, t: Throwable) {
                Toast.makeText(context, "SOS Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    // ----------------

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            statusMessage = "Getting GPS..."
            locationHelper.getCurrentLocation { lat, long ->
                currentLat = lat
                currentLng = long
                pickupText = "$lat, $long"
                statusMessage = "Location Found!"
            }
        }
    }

    // SCAFFOLD lets us add a Floating Button easily
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { triggerSOS() },
                containerColor = Color.Red,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Warning, contentDescription = "SOS")
            }
        }
    ) { innerPadding ->

        // MAIN CONTENT (The Form)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "CampusGuardian Request")

            Spacer(modifier = Modifier.height(24.dp))

            // MODE TOGGLE
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = { selectedMode = "CAR" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedMode == "CAR") Color(0xFF00695C) else Color.Gray
                    ),
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) { Text("RIDE🚗 ") }

                Button(
                    onClick = { selectedMode = "WALK" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedMode == "WALK") Color(0xFF00695C) else Color.Gray
                    ),
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) { Text("WALK🚶") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pickupText, onValueChange = { pickupText = it },
                label = { Text("Pickup Location") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                        statusMessage = "Getting GPS..."
                        locationHelper.getCurrentLocation { lat, long ->
                            currentLat = lat
                            currentLng = long
                            pickupText = "$lat, $long"
                        }
                    } else {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(" USE MY CURRENT LOCATION")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = destination, onValueChange = { destination = it },
                label = { Text("Destination") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    statusMessage = "Sending ${selectedMode} Request..."
                    sendRequest(pickupText, currentLat, currentLng, destination, selectedMode) { result ->
                        statusMessage = result
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("FIND ESCORT")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = statusMessage)
        }
    }
}

// Logic to send data
fun sendRequest(pickup: String, lat: Double, lng: Double, dest: String, mode: String, onResult: (String) -> Unit) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.107:8000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(ApiService::class.java)

    val requestData = RideRequest(
        student_name = "Gift",
        pickup_location = pickup,
        pickup_lat = lat,
        pickup_lng = lng,
        destination = dest,
        travel_mode = mode
    )

    api.requestRide(requestData).enqueue(object : Callback<RideResponse> {
        override fun onResponse(call: Call<RideResponse>, response: Response<RideResponse>) {
            if (response.isSuccessful) {
                onResult("Success! ${response.body()?.message}")
            } else {
                onResult("Error: ${response.code()}")
            }
        }
        override fun onFailure(call: Call<RideResponse>, t: Throwable) {
            onResult("Failed: ${t.message}")
        }
    })
}