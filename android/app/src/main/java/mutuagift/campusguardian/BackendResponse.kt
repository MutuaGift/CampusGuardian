package com.mutuagift.campusguardian

data class BackendResponse(
    val status: String,  // <--- Ensure this says "String" (Capital S)
    val message: String
)