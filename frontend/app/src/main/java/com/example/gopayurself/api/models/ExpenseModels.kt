package com.example.gopayurself.api.models

data class ExpenseApi(
    val id: String,
    val amount: Double,
    val date: String, // ISO date string
    val payer: User,
    val group: GroupSummary,
    val splits: List<ExpenseSplit>
)

data class GroupSummary(
    val id: String,
    val name: String
)

data class ExpenseSplit(
    val user: User,
    val amount: Double
)

data class ExpensesResponse(
    val expenses: List<ExpenseApi>
)

data class CreateExpenseRequest(
    val amount: Double,
    val groupId: String,
    val payerId: String,
    val splits: List<ExpenseSplitRequest>
)

data class ExpenseSplitRequest(
    val userId: String,
    val amount: Double
)

data class CreateExpenseResponse(
    val expense: ExpenseApi
)