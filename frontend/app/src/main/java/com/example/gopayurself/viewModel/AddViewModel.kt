package com.example.gopayurself.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gopayurself.api.TokenManager
import com.example.gopayurself.api.models.ExpenseApi
import com.example.gopayurself.api.models.GroupApi
import com.example.gopayurself.api.models.User
import com.example.gopayurself.models.ExpenseData
import com.example.gopayurself.models.Group
import com.example.gopayurself.repository.AuthRepository
import com.example.gopayurself.repository.ExpenseRepository
import com.example.gopayurself.repository.GroupRepository
import com.example.gopayurself.repository.UserRepository
import kotlinx.coroutines.launch
import java.util.UUID

class AppViewModel(application: android.app.Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(getApplication())
    private val authRepository = AuthRepository(tokenManager)
    private val userRepository = UserRepository()
    private val groupRepository = GroupRepository()
    private val expenseRepository = ExpenseRepository()

    var isLoggedIn by mutableStateOf(false)
        private set

    var currentUser by mutableStateOf<User?>(null)
        private set

    var groups by mutableStateOf(listOf<GroupApi>())
        private set

    var currentGroup by mutableStateOf<GroupApi?>(null)
        private set

    var currentGroupExpenses by mutableStateOf(listOf<ExpenseApi>())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = authRepository.login(com.example.gopayurself.api.models.LoginRequest(email, password))
                result.onSuccess {
                    isLoggedIn = true
                    loadUserAndGroups()
                }.onFailure { exception ->
                    errorMessage = exception.message ?: "Login failed"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Login failed"
            } finally {
                isLoading = false
            }
        }
    }

    fun signup(firstName: String, lastName: String, email: String, phoneNumber: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = authRepository.signup(com.example.gopayurself.api.models.SignupRequest(firstName, lastName, email, phoneNumber, password))
                result.onSuccess {
                    isLoggedIn = true
                    loadUserAndGroups()
                }.onFailure { exception ->
                    errorMessage = exception.message ?: "Signup failed"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Signup failed"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        authRepository.logout()
        isLoggedIn = false
        currentUser = null
        groups = emptyList()
        currentGroup = null
        errorMessage = null
    }

    fun createGroup(name: String, memberPhoneNumbers: List<String>) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = groupRepository.createGroup(com.example.gopayurself.api.models.CreateGroupRequest(name, memberPhoneNumbers))
                result.onSuccess { groupApi ->
                    groups = groups + groupApi
                }.onFailure { exception ->
                    errorMessage = exception.message ?: "Failed to create group"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to create group"
            } finally {
                isLoading = false
            }
        }
    }

    fun selectGroup(group: GroupApi) {
        currentGroup = group
        loadCurrentGroupExpenses()
    }

    fun clearCurrentGroup() {
        currentGroup = null
        currentGroupExpenses = emptyList()
    }

    fun addMemberToCurrentGroup(memberName: String) {
        // TODO: Implement API call to add member
        // For now, this is a stub
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val result = userRepository.getCurrentUser()
                result.onSuccess { user ->
                    currentUser = user
                }.onFailure { exception ->
                    errorMessage = exception.message ?: "Failed to load user"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load user"
            }
        }
    }

    private fun loadUserAndGroups() {
        viewModelScope.launch {
            try {
                val userResult = userRepository.getCurrentUser()
                userResult.onSuccess { user ->
                    currentUser = user
                    // now load groups
                    val groupResult = groupRepository.getGroups(user.id)
                    groupResult.onSuccess { groupList ->
                        groups = groupList
                    }.onFailure { exception ->
                        errorMessage = exception.message ?: "Failed to load groups"
                    }
                }.onFailure { exception ->
                    errorMessage = exception.message ?: "Failed to load user"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load data"
            }
        }
    }

    private fun loadUserGroups() {
        currentUser?.let { user ->
            viewModelScope.launch {
                try {
                    val result = groupRepository.getGroups(user.id)
                    result.onSuccess { groupList ->
                        groups = groupList
                    }.onFailure { exception ->
                        errorMessage = exception.message ?: "Failed to load groups"
                    }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Failed to load groups"
                }
            }
        }
    }

    private fun loadCurrentGroupExpenses() {
        currentGroup?.let { group ->
            viewModelScope.launch {
                try {
                    val result = expenseRepository.getExpenses(group.id)
                    result.onSuccess { expenses ->
                        currentGroupExpenses = expenses
                    }.onFailure { exception ->
                        errorMessage = exception.message ?: "Failed to load expenses"
                    }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Failed to load expenses"
                }
            }
        }
    }
    // Add these methods to your AppViewModel
    fun removeMemberFromCurrentGroup(memberName: String) {
        // TODO: Implement API call to remove member
        // For now, this is a stub
    }

    fun deleteCurrentGroup() {
        currentGroup?.let { groupToDelete ->
            viewModelScope.launch {
                isLoading = true
                errorMessage = null
                try {
                    val result = groupRepository.deleteGroup(groupToDelete.id)
                    result.onSuccess {
                        groups = groups.filter { it.id != groupToDelete.id }
                        currentGroup = null
                    }.onFailure { exception ->
                        errorMessage = exception.message ?: "Failed to delete group"
                    }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Failed to delete group"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    // In AppViewModel
    // In AppViewModel
    fun addExpenseToCurrentGroup(description: String, amount: Double, paidBy: String, participants: List<String>) {
        currentGroup?.let { group ->
            viewModelScope.launch {
                isLoading = true
                errorMessage = null
                try {
                    // Find payer user
                    val allUsers = listOf(group.owner) + group.members.map { it.user }
                    val payerUser = allUsers.find { "${it.firstName} ${it.lastName}" == paidBy }
                    if (payerUser == null) {
                        errorMessage = "Payer not found in group"
                        return@launch
                    }

                    // Find user IDs for participants (include owner)
                    val participantUsers = allUsers.filter { "${it.firstName} ${it.lastName}" in participants }
                    val splits = participantUsers.map { com.example.gopayurself.api.models.ExpenseSplitRequest(it.id, amount / participantUsers.size) }

                    val result = expenseRepository.createExpense(
                        com.example.gopayurself.api.models.CreateExpenseRequest(
                            amount = amount,
                            groupId = group.id,
                            payerId = payerUser.id,
                            splits = splits
                        )
                    )
                    result.onSuccess { expenseApi ->
                        // Reload expenses for current group
                        loadCurrentGroupExpenses()
                    }.onFailure { exception ->
                        errorMessage = exception.message ?: "Failed to add expense"
                    }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Failed to add expense"
                } finally {
                    isLoading = false
                }
            }
        }
    }
    fun payDebt(fromUser: String, toUser: String, amount: Double) {
        currentGroup?.let { group ->
            viewModelScope.launch {
                isLoading = true
                errorMessage = null
                try {
                    // Find user IDs - include owner
                    val allUsers = listOf(group.owner) + group.members.map { it.user }
                    val fromUserObj = allUsers.find { "${it.firstName} ${it.lastName}" == fromUser }
                    val toUserObj = allUsers.find { "${it.firstName} ${it.lastName}" == toUser }

                    if (fromUserObj != null && toUserObj != null) {
                        // When paying debt, the receiver (toUser) is the payer, and the payer (fromUser) owes
                        val splits = listOf(com.example.gopayurself.api.models.ExpenseSplitRequest(fromUserObj.id, amount))

                        val result = expenseRepository.createExpense(
                            com.example.gopayurself.api.models.CreateExpenseRequest(
                                amount = amount,
                                groupId = group.id,
                                payerId = toUserObj.id,  // toUser receives the payment
                                splits = splits  // fromUser owes the amount
                            )
                        )
                        result.onSuccess {
                            // Reload expenses for current group
                            loadCurrentGroupExpenses()
                        }.onFailure { exception ->
                            errorMessage = exception.message ?: "Failed to record payment"
                        }
                    } else {
                        errorMessage = "Users not found in group"
                    }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Failed to record payment"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = expenseRepository.deleteExpense(expenseId)
                result.onSuccess {
                    // Reload expenses for current group
                    loadCurrentGroupExpenses()
                }.onFailure { exception ->
                    errorMessage = exception.message ?: "Failed to delete expense"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to delete expense"
            } finally {
                isLoading = false
            }
        }
    }
}