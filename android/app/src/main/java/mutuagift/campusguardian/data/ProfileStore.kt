package com.mutuagift.campusguardian.data

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// A "Singleton" object exists as long as the app is running
object ProfileStore {
    // We use mutableStateOf so the UI updates automatically when this changes
    var profileImageUri: Uri? by mutableStateOf(null)
    var name: String by mutableStateOf("Gift Mutua")
    var bio: String by mutableStateOf("Computer Science Student")
}