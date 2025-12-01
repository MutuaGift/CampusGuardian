package com.mutuagift.campusguardian

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to CampusGuardian")

        Spacer(modifier = Modifier.height(32.dp))

        // Button 1: Go to Passenger Screen
        Button(
            onClick = { navController.navigate("passenger_screen") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("I NEED A RIDE")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button 2: Go to Driver Screen
        Button(
            onClick = { navController.navigate("driver_screen") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("I AM A DRIVER")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- NEW BUTTON 3: Go to Map ---
        Button(
            onClick = { navController.navigate("map_screen") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("VIEW CAMPUS MAP")
        }
    }
}