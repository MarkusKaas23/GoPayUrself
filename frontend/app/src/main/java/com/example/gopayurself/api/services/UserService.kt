package com.example.gopayurself.api.services

import com.example.gopayurself.api.models.User
import retrofit2.Response
import retrofit2.http.GET

interface UserService {
    @GET("users/me")
    suspend fun getCurrentUser(): Response<User>
}