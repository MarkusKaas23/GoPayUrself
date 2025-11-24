package com.example.gopayurself.repository

import com.example.gopayurself.api.ApiClient
import com.example.gopayurself.api.TokenManager
import com.example.gopayurself.api.models.AuthResponse
import com.example.gopayurself.api.models.LoginRequest
import com.example.gopayurself.api.models.SignupRequest

class AuthRepository(private val tokenManager: TokenManager) {

    suspend fun signup(request: SignupRequest): Result<AuthResponse> {
        return try {
            val response = ApiClient.authService.signup(request)
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    tokenManager.saveToken(authResponse.token)
                    Result.success(authResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Signup failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = ApiClient.authService.login(request)
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    tokenManager.saveToken(authResponse.token)
                    Result.success(authResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }
}