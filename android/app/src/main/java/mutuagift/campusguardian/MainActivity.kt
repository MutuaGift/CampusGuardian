package com.mutuagift.campusguardian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1. Create the Controller (The Brain of Navigation)
            val navController = rememberNavController()

            // 2. Setup the Map (NavHost)
            // startDestination = The screen to show first
            NavHost(navController = navController, startDestination = "home_screen") {

                // Route A: The Menu
                composable("home_screen") {
                    HomeScreen(navController)
                }

                // Route B: The Passenger Form
                composable("passenger_screen") {
                    RideRequestScreen()
                }

                // Route C: The Driver Dashboard
                composable("driver_screen") {
                    DriverDashboard()
                }
            }
        }
    }
}