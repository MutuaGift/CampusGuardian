package com.mutuagift.campusguardian.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mutuagift.campusguardian.data.ProfileStore // Import the Store

@Composable
fun EditProfileScreen(navController: NavController) {
    // LOAD CURRENT VALUES FROM STORE
    var name by remember { mutableStateOf(ProfileStore.name) }
    var bio by remember { mutableStateOf(ProfileStore.bio) }
    var selectedImageUri by remember { mutableStateOf(ProfileStore.profileImageUri) }

    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri = uri
        }
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // CLICKABLE IMAGE
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(selectedImageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Add Photo",
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }
        }
        Text(text = "Tap to change photo", color = Color.Gray, modifier = Modifier.padding(top = 8.dp))

        Spacer(modifier = Modifier.height(24.dp))

        // NAME INPUT
        OutlinedTextField(
            value = name, onValueChange = { name = it }, label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // BIO INPUT
        OutlinedTextField(
            value = bio, onValueChange = { bio = it }, label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // SAVE BUTTON (UPDATES THE STORE)
        Button(
            onClick = {
                // Save data to the Singleton
                ProfileStore.name = name
                ProfileStore.bio = bio
                ProfileStore.profileImageUri = selectedImageUri

                // Return to previous screen
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SAVE CHANGES")
        }
    }
}