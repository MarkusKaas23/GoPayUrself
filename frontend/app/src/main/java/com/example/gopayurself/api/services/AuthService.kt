package com.example.gopayurself.api.services

import com.example.gopayurself.api.models.AuthResponse
import com.example.gopayurself.api.models.LoginRequest
import com.example.gopayurself.api.models.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}