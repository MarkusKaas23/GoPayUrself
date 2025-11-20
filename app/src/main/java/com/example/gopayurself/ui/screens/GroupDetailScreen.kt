package com.example.gopayurself.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gopayurself.models.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    group: Group,
    currentUserEmail: String,
    onAddMember: (String) -> Unit,
    onRemoveMember: (String) -> Unit,
    onDeleteGroup: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var newMemberName by remember { mutableStateOf("") }
    var showAddExpenseButton by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMemberMenu by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf<String?>(null) }

    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme

    val isOwner = group.createdBy == currentUserEmail

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(group.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isOwner) {
                        IconButton(
                            onClick = { showDeleteDialog = true }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Group")
                        }
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
            // Group Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colors.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Total Expenses",
                        style = typography.titleMedium,
                        color = colors.onPrimaryContainer
                    )
                    Text(
                        "$${String.format("%.2f", group.totalExpenses)}",
                        style = typography.displayMedium,
                        color = colors.onPrimaryContainer
                    )
                    if (isOwner) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "You are the owner",
                            style = typography.labelSmall,
                            color = colors.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Members:",
                style = typography.titleMedium,
                color = colors.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            group.members.forEach { member ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "• $member ${if (member == group.createdBy) "(Owner)" else ""}",
                        style = typography.bodyLarge,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )

                    if (isOwner && member != group.createdBy) {
                        IconButton(
                            onClick = {
                                selectedMember = member
                                showMemberMenu = true
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove Member",
                                tint = colors.error
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isOwner) {
                OutlinedTextField(
                    value = newMemberName,
                    onValueChange = { newMemberName = it },
                    label = { Text("Add Member", style = typography.labelLarge) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (newMemberName.isNotBlank()) {
                            onAddMember(newMemberName.trim())
                            newMemberName = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                ) {
                    Text("Add Member", style = typography.labelLarge)
                }
            }

            if (showAddExpenseButton) {
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = { /* TODO: Navigate to add expense */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colors.secondary
                    )
                ) {
                    Text("Add Expense (Coming Soon)", style = typography.labelLarge)
                }
            }
        }
    }

    // Confirmation dialog for deleting group
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Group") },
            text = { Text("Are you sure you want to delete this group? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteGroup()
                    }
                ) {
                    Text("Delete", color = colors.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirmation dialog for removing member
    if (showMemberMenu && selectedMember != null) {
        AlertDialog(
            onDismissRequest = {
                showMemberMenu = false
                selectedMember = null
            },
            title = { Text("Remove Member") },
            text = { Text("Are you sure you want to remove $selectedMember from the group?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemoveMember(selectedMember!!)
                        showMemberMenu = false
                        selectedMember = null
                    }
                ) {
                    Text("Remove", color = colors.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showMemberMenu = false
                        selectedMember = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}