package com.example.gopayurself.api.models

data class AuthResponse(
    val token: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignupRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String
)