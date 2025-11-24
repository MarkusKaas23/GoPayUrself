package com.example.gopayurself.api.services

import com.example.gopayurself.api.models.CreateExpenseRequest
import com.example.gopayurself.api.models.CreateExpenseResponse
import com.example.gopayurself.api.models.ExpensesResponse
import retrofit2.Response
import retrofit2.http.*

interface ExpenseService {
    @GET("expenses")
    suspend fun getExpenses(@Query("groupId") groupId: String): Response<ExpensesResponse>

    @POST("expenses")
    suspend fun createExpense(@Body request: CreateExpenseRequest): Response<CreateExpenseResponse>

    @DELETE("expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: String): Response<Unit>
}