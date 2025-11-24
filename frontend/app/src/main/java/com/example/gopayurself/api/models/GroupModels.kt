package com.example.gopayurself.api.models

data class GroupApi(
    val id: String,
    val name: String,
    val ownerId: String,
    val owner: User,
    val members: List<GroupMember>
)

data class GroupMember(
    val user: User
)

data class GroupsResponse(
    val groups: List<GroupApi>
)

data class CreateGroupRequest(
    val name: String,
    val memberPhoneNumbers: List<String>
)

data class CreateGroupResponse(
    val group: GroupApi
)