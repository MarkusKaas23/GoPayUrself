package com.example.gopayurself.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.gopayurself.models.ExpenseData
import com.example.gopayurself.models.Group
import java.util.UUID

class AppViewModel : ViewModel() {
    var isLoggedIn by mutableStateOf(false)
        private set

    var currentUserEmail by mutableStateOf("")
        private set

    var groups by mutableStateOf(listOf<Group>())
        private set

    var currentGroup by mutableStateOf<Group?>(null)
        private set

    fun login(email: String) {
        currentUserEmail = email
        isLoggedIn = true
        // In a real app, load user's groups from backend
        loadUserGroups()
    }

    fun signup(email: String) {
        currentUserEmail = email
        isLoggedIn = true
        loadUserGroups()
    }

    fun logout() {
        currentUserEmail = ""
        isLoggedIn = false
        groups = emptyList()
        currentGroup = null
    }

    fun createGroup(name: String, members: List<String>) {
        val newGroup = Group(
            id = UUID.randomUUID().toString(),
            name = name,
            members = members,
            createdBy = currentUserEmail
        )
        groups = groups + newGroup
        //  Todo: save to backend
    }

    fun selectGroup(group: Group) {
        currentGroup = group
    }

    fun clearCurrentGroup() {
        currentGroup = null
    }

    fun addMemberToCurrentGroup(memberName: String) {
        currentGroup?.let { group ->
            val updatedGroup = group.copy(
                members = group.members + memberName
            )
            groups = groups.map {
                if (it.id == group.id) updatedGroup else it
            }
            currentGroup = updatedGroup
        }
    }

    private fun loadUserGroups() {
        // Simulate loading groups - Todo: fetch from backend
        // For demo purposes, a sample group is created
        groups = listOf(
            Group(
                id = UUID.randomUUID().toString(),
                name = "Test Group",
                members = listOf("Silas", "Alex"),
                totalExpenses = 15000.69,
                createdBy = currentUserEmail
            )
        )
    }
    // Add these methods to your AppViewModel
    fun removeMemberFromCurrentGroup(memberName: String) {
        currentGroup?.let { group ->
            val updatedGroup = group.copy(
                members = group.members.filter { it != memberName }
            )
            groups = groups.map {
                if (it.id == group.id) updatedGroup else it
            }
            currentGroup = updatedGroup
        }
    }

    fun deleteCurrentGroup() {
        currentGroup?.let { groupToDelete ->
            groups = groups.filter { it.id != groupToDelete.id }
            currentGroup = null
        }
    }

    // In AppViewModel
    // In AppViewModel
    fun addExpenseToCurrentGroup(description: String, amount: Double, paidBy: String, participants: List<String>) {
        currentGroup?.let { group ->
            val newExpense = ExpenseData(
                description = description,
                amount = amount,
                paidBy = paidBy,
                participants = participants
            )
            val updatedGroup = group.copy(
                totalExpenses = group.totalExpenses + amount,
                expenses = group.expenses + newExpense
            )

            groups = groups.map {
                if (it.id == group.id) updatedGroup else it
            }
            currentGroup = updatedGroup
        }
    }
    fun createGroup(name: String, members: List<String>, expenses: List<ExpenseData> = emptyList()) {
        // Calculate total expenses
        val totalExpenses = expenses.sumOf { it.amount }

        val newGroup = Group(
            id = UUID.randomUUID().toString(),
            name = name,
            members = members,
            totalExpenses = totalExpenses,
            createdBy = currentUserEmail,
            expenses = expenses
        )
        groups = groups + newGroup
    }
    fun payDebt(fromUser: String, toUser: String, amount: Double) {
        currentGroup?.let { group ->
            // Create a settlement expense where the debtor pays the creditor
            val settlementExpense = ExpenseData(
                description = "Payment from $fromUser to $toUser",
                amount = amount,
                paidBy = fromUser, // The person paying is recorded as the "payer"
                participants = listOf(toUser) // Only the recipient gets the money
            )

            val updatedGroup = group.copy(
                expenses = group.expenses + settlementExpense
            )

            groups = groups.map {
                if (it.id == group.id) updatedGroup else it
            }
            currentGroup = updatedGroup
        }
    }
}