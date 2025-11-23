package com.example.gopayurself.models

data class Group(
    val id: String,
    val name: String,
    val members: List<String>,
    val totalExpenses: Double = 0.0,
    val createdBy: String,
    val createdAt: Long = System.currentTimeMillis(),
    val expenses: List<ExpenseData> = emptyList()

)

data class ExpenseData(
    val description: String,
    val amount: Double,
    val paidBy: String,
    val participants: List<String> // Only these people owe money for this expense
)