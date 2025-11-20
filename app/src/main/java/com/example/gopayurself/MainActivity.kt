package com.example.gopayurself

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gopayurself.navigation.Screen
import com.example.gopayurself.ui.screens.*
import com.example.gopayurself.ui.theme.GoPayUrselfTheme
import com.example.gopayurself.viewmodels.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}

@Composable
fun AppNavigation(viewModel: AppViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

    when (currentScreen) {
        Screen.Login -> {
            LoginScreen(
                onLoginSuccess = { email ->
                    viewModel.login(email)
                    currentScreen = Screen.Dashboard
                },
                onNavigateToSignup = { // ADD THIS PARAMETER
                    currentScreen = Screen.Signup
                }
            )
        }

        Screen.Signup -> {
            SignupScreen(
                onSignupSuccess = { email ->
                    viewModel.signup(email)
                    currentScreen = Screen.Dashboard
                },
                onNavigateToLogin = {
                    currentScreen = Screen.Login
                }
            )
        }

        Screen.Dashboard -> {
            DashboardScreen(
                userEmail = viewModel.currentUserEmail,
                groups = viewModel.groups,
                onCreateGroup = {
                    currentScreen = Screen.CreateGroup
                },
                onGroupClick = { group ->
                    viewModel.selectGroup(group)
                    currentScreen = Screen.GroupDetail
                },
                onLogout = {
                    viewModel.logout()
                    currentScreen = Screen.Login
                }
            )
        }

        Screen.CreateGroup -> {
            CreateGroupScreen(
                onGroupCreated = { name, members ->
                    viewModel.createGroup(name, members)
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
                    currentUserEmail = viewModel.currentUserEmail, // Pass current user email
                    onAddMember = { memberName ->
                        viewModel.addMemberToCurrentGroup(memberName)
                    },
                    onRemoveMember = { memberName -> // Add this callback
                        viewModel.removeMemberFromCurrentGroup(memberName)
                    },
                    onDeleteGroup = { // Add this callback
                        viewModel.deleteCurrentGroup()
                        currentScreen = Screen.Dashboard
                    },
                    onNavigateBack = {
                        viewModel.clearCurrentGroup()
                        currentScreen = Screen.Dashboard
                    }
                )
            }
        }
    }
}



