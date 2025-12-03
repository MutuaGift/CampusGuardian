package com.mutuagift.campusguardian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
// IMPORTS FOR ARGUMENTS
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.mutuagift.campusguardian.ui.LoginScreen
import com.mutuagift.campusguardian.ui.SignUpScreen
import com.mutuagift.campusguardian.ui.HomeScreen
import com.mutuagift.campusguardian.ui.RideRequestScreen
import com.mutuagift.campusguardian.ui.DriverDashboard
import com.mutuagift.campusguardian.ui.MapScreen
import com.mutuagift.campusguardian.ui.ProfileScreen
import com.mutuagift.campusguardian.ui.EditProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "login_screen") {

                composable("login_screen") { LoginScreen(navController) }
                composable("signup_screen") { SignUpScreen(navController) }
                composable("home_screen") { HomeScreen(navController) }
                composable("passenger_screen") { RideRequestScreen() }

                // PASS NAV CONTROLLER TO DRIVER DASHBOARD
                composable("driver_screen") { DriverDashboard(navController) }

                // PROFILE ROUTES
                composable("profile_screen") { ProfileScreen(navController) }
                composable("edit_profile_screen") { EditProfileScreen(navController) }

                // --- THE DYNAMIC MAP ROUTE ---
                // It expects two numbers: lat and lng
                composable(
                    route = "map_screen/{lat}/{lng}",
                    arguments = listOf(
                        navArgument("lat") { type = NavType.FloatType },
                        navArgument("lng") { type = NavType.FloatType }
                    )
                ) { backStackEntry ->
                    // Extract the numbers
                    val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0470
                    val lng = backStackEntry.arguments?.getFloat("lng")?.toDouble() ?: 37.6498

                    // Show map
                    MapScreen(lat, lng)
                }
            }
        }
    }
}