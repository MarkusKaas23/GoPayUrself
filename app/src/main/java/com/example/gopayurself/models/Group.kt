package com.example.gopayurself.models

data class Group(
    val id: String,
    val name: String,
    val members: List<String>,
    val totalExpenses: Double = 0.0,
    val createdBy: String,
    val createdAt: Long = System.currentTimeMillis()
)