package com.example.gopayurself.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onGroupCreated: (String, List<String>) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var groupName by remember { mutableStateOf("") }
    var newMemberName by remember { mutableStateOf("") }
    var members by remember { mutableStateOf(listOf<String>()) }

    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Group") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    titleContentColor = colors.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name") },
                placeholder = { Text("e.g., Weekend Trip, Office Lunch") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.outline
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Members:",
                style = typography.titleMedium,
                color = colors.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (members.isEmpty()) {
                Text(
                    "No members added yet",
                    style = typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            } else {
                members.forEach { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "• $member",
                            style = typography.bodyLarge,
                            color = colors.onSurface
                        )
                        IconButton(
                            onClick = { members = members - member },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = colors.error
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newMemberName,
                onValueChange = { newMemberName = it },
                label = { Text("Add Member") },
                placeholder = { Text("Enter name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.outline
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (newMemberName.isNotBlank()) {
                        members = members + newMemberName.trim()
                        newMemberName = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.secondary,
                    contentColor = colors.onSecondary
                )
            ) {
                Text("Add Member")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (groupName.isNotBlank()) {
                        onGroupCreated(groupName.trim(), members)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = groupName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text("Create Group", style = typography.labelLarge)
            }
        }
    }
}