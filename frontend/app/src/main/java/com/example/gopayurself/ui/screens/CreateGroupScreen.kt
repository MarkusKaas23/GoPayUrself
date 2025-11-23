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
import com.example.gopayurself.models.ExpenseData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onGroupCreated: (String, List<String>, List<ExpenseData>) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var groupName by remember { mutableStateOf("") }
    var newMemberName by remember { mutableStateOf("") }
    var members by remember { mutableStateOf(listOf<String>()) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var expenses by remember { mutableStateOf(listOf<ExpenseData>()) }

    // Expense state variables
    var expenseDescription by remember { mutableStateOf("") }
    var expenseAmount by remember { mutableStateOf("") }
    var expensePaidBy by remember { mutableStateOf("") }
    var selectedParticipants by remember { mutableStateOf<Set<String>>(emptySet()) }

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
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            "• $member",
                            style = typography.bodyLarge,
                            color = colors.onSurface,
                            modifier = Modifier.weight(1f)
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

            // Add Member
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = newMemberName,
                    onValueChange = { newMemberName = it },
                    label = { Text("Add Member") },
                    placeholder = { Text("Enter name") },
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
                        if (newMemberName.isNotBlank()) {
                            members = members + newMemberName.trim()
                            newMemberName = ""
                        }
                    },
                    enabled = newMemberName.isNotBlank()
                ) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Expenses Section
            Text(
                "Initial Expenses (Optional):",
                style = typography.titleMedium,
                color = colors.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (expenses.isEmpty()) {
                Text(
                    "No expenses added yet",
                    style = typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            } else {
                expenses.forEachIndexed { index, expense ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colors.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row {
                                Text(
                                    expense.description,
                                    style = typography.bodyMedium,
                                    color = colors.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "$${String.format("%.2f", expense.amount)}",
                                    style = typography.bodyMedium,
                                    color = colors.primary
                                )
                            }
                            Text(
                                "Paid by: ${expense.paidBy}",
                                style = typography.bodySmall,
                                color = colors.onSurfaceVariant
                            )
                            Text(
                                "Split between: ${expense.participants.joinToString(", ")}",
                                style = typography.bodySmall,
                                color = colors.onSurfaceVariant
                            )
                            Row {
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = {
                                        expenses = expenses.filterIndexed { i, _ -> i != index }
                                    }
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove expense")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (members.isNotEmpty()) {
                        showAddExpenseDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = members.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.secondary,
                    contentColor = colors.onSecondary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Expense", style = typography.labelLarge)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (groupName.isNotBlank() && members.isNotEmpty()) {
                        onGroupCreated(groupName.trim(), members, expenses)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = groupName.isNotBlank() && members.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text("Create Group", style = typography.labelLarge)
            }
        }
    }

    // Add Expense Dialog
    if (showAddExpenseDialog) {
        AlertDialog(
            onDismissRequest = { showAddExpenseDialog = false },
            title = { Text("Add Expense") },
            text = {
                Column {
                    OutlinedTextField(
                        value = expenseDescription,
                        onValueChange = { expenseDescription = it },
                        label = { Text("What was it for?") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = expenseAmount,
                        onValueChange = { expenseAmount = it },
                        label = { Text("Amount") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = expensePaidBy,
                        onValueChange = { expensePaidBy = it },
                        label = { Text("Who paid?") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Who should split this expense?", style = typography.labelMedium)

                    members.forEach { member ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = selectedParticipants.contains(member),
                                onCheckedChange = { checked ->
                                    selectedParticipants = if (checked) {
                                        selectedParticipants + member
                                    } else {
                                        selectedParticipants - member
                                    }
                                }
                            )
                            Text(
                                text = member,
                                style = typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (expenseDescription.isNotBlank() && expenseAmount.isNotBlank() &&
                            expensePaidBy.isNotBlank() && selectedParticipants.isNotEmpty()) {
                            val amount = expenseAmount.toDoubleOrNull() ?: 0.0
                            val newExpense = ExpenseData(
                                description = expenseDescription,
                                amount = amount,
                                paidBy = expensePaidBy,
                                participants = selectedParticipants.toList()
                            )
                            expenses = expenses + newExpense

                            // Reset form
                            expenseDescription = ""
                            expenseAmount = ""
                            expensePaidBy = ""
                            selectedParticipants = emptySet()
                            showAddExpenseDialog = false
                        }
                    },
                    enabled = expenseDescription.isNotBlank() && expenseAmount.isNotBlank() &&
                            expensePaidBy.isNotBlank() && selectedParticipants.isNotEmpty()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddExpenseDialog = false
                        expenseDescription = ""
                        expenseAmount = ""
                        expensePaidBy = ""
                        selectedParticipants = emptySet()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}