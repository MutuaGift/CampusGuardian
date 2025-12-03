package com.mutuagift.campusguardian.ui

import com.mutuagift.campusguardian.data.BackendResponse
import com.mutuagift.campusguardian.data.Ride
import com.mutuagift.campusguardian.network.ApiService

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController // <--- Needed for navigation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
// 1. Accept the NavController here
fun DriverDashboard(navController: NavController) {
    var ridesList by remember { mutableStateOf<List<Ride>>(emptyList()) }
    var statusText by remember { mutableStateOf("Loading rides...") }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    // FETCH LOGIC
    fun fetchRides() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.107:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)

        val call = if (searchQuery.isEmpty()) api.getOpenRides() else api.searchRides(searchQuery)

        call.enqueue(object : Callback<List<Ride>> {
            override fun onResponse(call: Call<List<Ride>>, response: Response<List<Ride>>) {
                if (response.isSuccessful) {
                    val allRides = response.body() ?: emptyList()
                    ridesList = allRides.filter { it.status != "COMPLETED" }
                        .sortedByDescending { it.status == "SOS_PENDING" }
                    statusText = "Found ${ridesList.size} active tasks"
                }
            }
            override fun onFailure(call: Call<List<Ride>>, t: Throwable) {
                statusText = "Error: ${t.message}"
            }
        })
    }

    // ACCEPT LOGIC
    fun acceptRide(rideId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.107:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)

        api.acceptRide(rideId).enqueue(object : Callback<BackendResponse> {
            override fun onResponse(call: Call<BackendResponse>, response: Response<BackendResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Ride Started!", Toast.LENGTH_SHORT).show()
                    fetchRides()
                }
            }
            override fun onFailure(call: Call<BackendResponse>, t: Throwable) {
                Toast.makeText(context, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // COMPLETE LOGIC
    fun completeRide(rideId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.107:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)

        api.completeRide(rideId).enqueue(object : Callback<BackendResponse> {
            override fun onResponse(call: Call<BackendResponse>, response: Response<BackendResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Ride Finished!", Toast.LENGTH_SHORT).show()
                    fetchRides()
                }
            }
            override fun onFailure(call: Call<BackendResponse>, t: Throwable) {
                Toast.makeText(context, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    LaunchedEffect(Unit) { fetchRides() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Community Dashboard", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Filter (e.g. Juja)") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { fetchRides() }) { Text("REFRESH") }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = statusText, color = Color.Gray)
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ridesList) { ride ->
                RideCard(
                    ride = ride,
                    onAcceptClick = { acceptRide(ride._id) },
                    onCompleteClick = { completeRide(ride._id) },
                    // 2. NAVIGATE TO MAP with coordinates
                    onMapClick = {
                        navController.navigate("map_screen/${ride.pickup_lat}/${ride.pickup_lng}")
                    }
                )
            }
        }
    }
}

@Composable
fun RideCard(
    ride: Ride,
    onAcceptClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onMapClick: () -> Unit // <--- New Callback
) {
    val cardColor = when(ride.status) {
        "IN_PROGRESS" -> Color(0xFFE8F5E9)
        "SOS_PENDING" -> Color(0xFFFFEBEE)
        else -> Color.White
    }

    val borderStroke = if (ride.status == "SOS_PENDING") BorderStroke(2.dp, Color.Red) else null

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = borderStroke
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            if (ride.status == "SOS_PENDING") {
                Text(text = "⚠️ EMERGENCY ALERT", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(text = ride.student, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            val modeIcon = when(ride.travel_mode) {
                "WALK" -> "🚶 WALKING BUDDY"
                "BODA" -> "🏍️ BODABODA"
                "SOS" -> "🚨 EMERGENCY"
                else -> "🚗 CAR RIDE"
            }
            Text(text = "Mode: $modeIcon", color = Color.DarkGray, fontSize = 14.sp)

            if (ride.status == "SOS_PENDING") {
                Text(text = "Loc: ${ride.pickup_lat}, ${ride.pickup_lng}")
            } else {
                Text(text = "From: ${ride.pickup} -> To: ${ride.destination}")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. SHOW MAP BUTTON (Only if coordinates exist)
            if (ride.pickup_lat != 0.0) {
                OutlinedButton(
                    onClick = onMapClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🗺️ VIEW ON MAP")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            when (ride.status) {
                "PENDING" -> {
                    Button(
                        onClick = onAcceptClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("ACCEPT RIDE") }
                }
                "SOS_PENDING" -> {
                    Button(
                        onClick = onAcceptClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("RESPOND TO SOS") }
                }
                "IN_PROGRESS" -> {
                    Button(
                        onClick = onCompleteClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("🏁 COMPLETE RIDE") }
                }
            }
        }
    }
}