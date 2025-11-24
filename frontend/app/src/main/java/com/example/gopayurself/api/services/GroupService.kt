package com.example.gopayurself.api.services

import com.example.gopayurself.api.models.CreateGroupRequest
import com.example.gopayurself.api.models.CreateGroupResponse
import com.example.gopayurself.api.models.GroupsResponse
import retrofit2.Response
import retrofit2.http.*

interface GroupService {
    @GET("groups")
    suspend fun getGroups(@Query("userId") userId: String): Response<GroupsResponse>

    @POST("groups")
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<CreateGroupResponse>

    @DELETE("groups/{id}")
    suspend fun deleteGroup(@Path("id") id: String): Response<Unit>
}