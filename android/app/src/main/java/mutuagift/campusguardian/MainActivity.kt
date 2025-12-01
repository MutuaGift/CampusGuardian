package com.mutuagift.campusguardian

// Import your UI screens from the 'ui' package
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mutuagift.campusguardian.ui.EditProfileScreen
import com.mutuagift.campusguardian.ui.HomeScreen
import com.mutuagift.campusguardian.ui.ProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1. Create the Controller
            val navController = rememberNavController()

            // 2. Setup the Navigation Host
            NavHost(navController = navController, startDestination = "home_screen") {

                // Route A: Menu
                composable("home_screen") {
                    HomeScreen(navController)
                }

                // Route B: Passenger Form
                composable("passenger_screen") {
                    RideRequestScreen()
                }

                // Route C: Driver Dashboard
                composable("driver_screen") {
                    DriverDashboard()
                }

                // Route D: Map Screen
                composable("map_screen") {
                    MapScreen()
                }

                // Route E: Profile (FIXED)
                composable("profile_screen") {
                    ProfileScreen(navController)
                }

                // Route F: Edit Profile
                composable("edit_profile_screen") {
                    EditProfileScreen(navController)
                }
            }
        }
    }
}