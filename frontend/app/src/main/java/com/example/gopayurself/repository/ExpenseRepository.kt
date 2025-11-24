package com.example.gopayurself.repository

import com.example.gopayurself.api.ApiClient
import com.example.gopayurself.api.models.CreateExpenseRequest
import com.example.gopayurself.api.models.ExpenseApi

class ExpenseRepository {

    suspend fun getExpenses(groupId: String): Result<List<ExpenseApi>> {
        return try {
            val response = ApiClient.expenseService.getExpenses(groupId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it.expenses) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to get expenses: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createExpense(request: CreateExpenseRequest): Result<ExpenseApi> {
        return try {
            val response = ApiClient.expenseService.createExpense(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it.expense) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to create expense: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteExpense(expenseId: String): Result<Unit> {
        return try {
            val response = ApiClient.expenseService.deleteExpense(expenseId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete expense: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}