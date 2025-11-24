package com.example.gopayurself.repository

import com.example.gopayurself.api.ApiClient
import com.example.gopayurself.api.models.User

class UserRepository {

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = ApiClient.userService.getCurrentUser()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to get user: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}