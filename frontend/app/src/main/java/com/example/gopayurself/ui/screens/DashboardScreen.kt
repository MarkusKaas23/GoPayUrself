package com.example.gopayurself.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gopayurself.api.models.GroupApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userEmail: String,
    groups: List<GroupApi>,
    errorMessage: String?,
    onCreateGroup: () -> Unit,
    onGroupClick: (Group) -> Unit,
>>>>>>> origin/master
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Groups", style = typography.titleLarge) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = colors.onSurface
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = colors.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    titleContentColor = colors.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateGroup,
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Group")
            }
        }
    ) { paddingValues ->
        errorMessage?.let { error ->
            Text(
                text = error,
                color = colors.error,
                style = typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (groups.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "No groups yet",
                        style = typography.headlineSmall,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        "Create your first group to start sharing expenses",
                        style = typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(groups) { group ->
                    GroupCard(
                        group = group,
                        onClick = { onGroupClick(group) }
                    )
                }
            }
        }
    }
}

@Composable
fun GroupCard(
    group: GroupApi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = group.name,
                style = typography.titleLarge,
                color = colors.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${group.members.size + 1} members",
                style = typography.bodyMedium,
                color = colors.onSurfaceVariant
            )

            // TODO: Fetch and display total expenses for group
        }
    }
}
