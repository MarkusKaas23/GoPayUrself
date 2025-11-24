package com.example.gopayurself.repository

import com.example.gopayurself.api.ApiClient
import com.example.gopayurself.api.models.CreateGroupRequest
import com.example.gopayurself.api.models.GroupApi
import com.example.gopayurself.api.models.GroupsResponse

class GroupRepository {

    suspend fun getGroups(userId: String): Result<List<GroupApi>> {
        return try {
            val response = ApiClient.groupService.getGroups(userId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it.groups) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to get groups: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createGroup(request: CreateGroupRequest): Result<GroupApi> {
        return try {
            val response = ApiClient.groupService.createGroup(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it.group) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to create group: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteGroup(groupId: String): Result<Unit> {
        return try {
            val response = ApiClient.groupService.deleteGroup(groupId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete group: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}