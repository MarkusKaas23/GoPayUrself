package com.example.gopayurself.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
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
    var newMemberPhone by remember { mutableStateOf("") }
    var memberPhones by remember { mutableStateOf(listOf<String>()) }


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
            // Group Name
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

            // Members Section
            Text(
                "Members (Phone Numbers):",
                style = typography.titleMedium,
                color = colors.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (memberPhones.isEmpty()) {
                Text(
                    "No members added yet",
                    style = typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            } else {
                memberPhones.forEach { phone ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            "• $phone",
                            style = typography.bodyLarge,
                            color = colors.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { memberPhones = memberPhones - phone },
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

            // Add Member
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = newMemberPhone,
                    onValueChange = { newMemberPhone = it },
                    label = { Text("Add Member") },
                    placeholder = { Text("Enter phone number") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (newMemberPhone.isNotBlank()) {
                            memberPhones = memberPhones + newMemberPhone.trim()
                            newMemberPhone = ""
                        }
                    },
                    enabled = newMemberPhone.isNotBlank()
                ) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (groupName.isNotBlank() && memberPhones.isNotEmpty()) {
                        onGroupCreated(groupName.trim(), memberPhones)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = groupName.isNotBlank() && memberPhones.isNotEmpty(),
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