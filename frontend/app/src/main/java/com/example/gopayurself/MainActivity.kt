package com.example.gopayurself

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gopayurself.api.ApiClient
import com.example.gopayurself.api.models.GroupApi
import com.example.gopayurself.navigation.Screen
import com.example.gopayurself.ui.screens.*
import com.example.gopayurself.ui.theme.GoPayUrselfTheme
import com.example.gopayurself.utils.NotificationHelper
import com.example.gopayurself.viewmodels.AppViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, notifications will work
        } else {
            // Permission denied, show a message or handle gracefully
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize API client
        ApiClient.init(this)

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        // Request notification permission for Android 13+
        requestNotificationPermission()

        enableEdgeToEdge()
        setContent {
            GoPayUrselfTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                else -> {
                    // Request permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: AppViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

    when (currentScreen) {
        Screen.Login -> {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    currentScreen = Screen.Dashboard
                },
                onNavigateToSignup = {
                    currentScreen = Screen.Signup
                }
            )
        }

        Screen.Signup -> {
            SignupScreen(
                viewModel = viewModel,
                onSignupSuccess = {
                    currentScreen = Screen.Dashboard
                },
                onNavigateToLogin = {
                    currentScreen = Screen.Login
                }
            )
        }

        Screen.Dashboard -> {
            DashboardScreen(
                userEmail = viewModel.currentUser?.email ?: "",
                groups = viewModel.groups,
                errorMessage = viewModel.errorMessage,
                onCreateGroup = {
                    currentScreen = Screen.CreateGroup
                },
                onGroupClick = { group: GroupApi ->
                    viewModel.selectGroup(group)
                    currentScreen = Screen.GroupDetail
                },
                onNavigateToSettings = {
                    currentScreen = Screen.UserSettings
                },
                onLogout = {
                    viewModel.logout()
                    currentScreen = Screen.Login
                }
            )
        }

        Screen.CreateGroup -> {
            CreateGroupScreen(
                onGroupCreated = { name, memberPhones ->
                    viewModel.createGroup(name, memberPhones)
                    currentScreen = Screen.Dashboard
                },
                onNavigateBack = {
                    currentScreen = Screen.Dashboard
                }
            )
        }

        Screen.GroupDetail -> {
            viewModel.currentGroup?.let { group ->
                GroupDetailScreen(
                    group = group,
                    expenses = viewModel.currentGroupExpenses,
                    currentUserEmail = viewModel.currentUser?.email ?: "",
                    onAddMember = { memberName ->
                        viewModel.addMemberToCurrentGroup(memberName)
                    },
                    onRemoveMember = { memberName ->
                        viewModel.removeMemberFromCurrentGroup(memberName)
                    },
                    onDeleteGroup = {
                        viewModel.deleteCurrentGroup()
                        currentScreen = Screen.Dashboard
                    },
                    onAddExpense = { description, amount, paidBy, participants ->
                        viewModel.addExpenseToCurrentGroup(description, amount, paidBy, participants)
                    },
                    onPayDebt = { fromUser, toUser, amount ->
                        viewModel.payDebt(fromUser, toUser, amount)
                    },
                    onDeleteExpense = { expenseId ->
                        viewModel.deleteExpense(expenseId)
                    },
                    onSendReminder = { userName, amount ->
                        viewModel.sendPaymentReminder(userName, amount)
                    },
                    onNavigateBack = {
                        viewModel.clearCurrentGroup()
                        currentScreen = Screen.Dashboard
                    }
                )
            }
        }
        Screen.UserSettings -> {
            UserSettingsScreen(
                userEmail = viewModel.currentUser?.email ?: "",
                firstName = viewModel.currentUser?.firstName ?: "",
                lastName = viewModel.currentUser?.lastName ?: "",
                phoneNumber = viewModel.currentUser?.phoneNumber ?: "",
                notificationsEnabled = viewModel.notificationsEnabled,
                onNotificationToggle = { enabled ->
                    viewModel.toggleNotifications(enabled)
                },
                onNavigateBack = {
                    currentScreen = Screen.Dashboard
                },
                onLogout = {
                    viewModel.logout()
                    currentScreen = Screen.Login
                }
            )
        }
    }
}