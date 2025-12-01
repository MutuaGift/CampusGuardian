package com.mutuagift.campusguardian.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    // We use a Box so we can float the Profile Icon in the corner
    Box(modifier = Modifier.fillMaxSize()) {

        // --- TOP RIGHT ICON ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End // Push to right side
        ) {
            IconButton(onClick = { navController.navigate("profile_screen") }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // --- CENTER BUTTONS ---
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome to CampusGuardian")

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("passenger_screen") },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("I NEED A RIDE")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("driver_screen") },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("I AM A DRIVER")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("map_screen") },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("VIEW CAMPUS MAP")
            }
        }
    }
}