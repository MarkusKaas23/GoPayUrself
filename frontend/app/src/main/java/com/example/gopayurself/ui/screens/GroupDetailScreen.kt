package com.example.gopayurself.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gopayurself.models.Group
import com.example.gopayurself.models.ExpenseData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    group: Group,
    currentUserEmail: String,
    onAddMember: (String) -> Unit,
    onRemoveMember: (String) -> Unit,
    onDeleteGroup: () -> Unit,
    onAddExpense: (String, Double, String, List<String>) -> Unit,
    onPayDebt: (String, String, Double) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var newMemberName by remember { mutableStateOf("") }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMemberMenu by remember { mutableStateOf(false) }
    var showPayDialog by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf<String?>(null) }
    var memberToPay by remember { mutableStateOf<String?>(null) }
    var amountToPay by remember { mutableStateOf(0.0) }

    // Expense state variables
    var expenseDescription by remember { mutableStateOf("") }
    var expenseAmount by remember { mutableStateOf("") }
    var expensePaidBy by remember { mutableStateOf("") }
    var selectedParticipants by remember { mutableStateOf<Set<String>>(emptySet()) }

    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme

    val isOwner = group.createdBy == currentUserEmail

    // Calculate balances with participant selection
    val memberBalances = calculateBalancesWithParticipants(group)
    val currentUserBalance = memberBalances[currentUserEmail] ?: 0.0

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

                    // Show current user's balance
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        when {
                            currentUserBalance > 0 -> "You are owed $${String.format("%.2f", currentUserBalance)}"
                            currentUserBalance < 0 -> "You owe $${String.format("%.2f", -currentUserBalance)}"
                            else -> "You're all settled up"
                        },
                        style = typography.labelMedium,
                        color = colors.onPrimaryContainer.copy(alpha = 0.8f)
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

            // Expenses Section
            Text(
                "Recent Expenses:",
                style = typography.titleMedium,
                color = colors.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (group.expenses.isEmpty()) {
                Text(
                    "No expenses yet. Add your first expense!",
                    style = typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            } else {
                group.expenses.takeLast(5).reversed().forEach { expense ->
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
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showAddExpenseDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.secondary,
                    contentColor = colors.onSecondary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Expense", style = typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Members section with Pay buttons
            Text(
                "Members:",
                style = typography.titleMedium,
                color = colors.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Show balances for each member with Pay buttons
            memberBalances.forEach { (member, balance) ->
                // Show Pay button if current user owes money to this member
                val shouldShowPayButton = balance > 0 && member != currentUserEmail

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            member == currentUserEmail -> colors.primaryContainer.copy(alpha = 0.3f)
                            else -> colors.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "$member ${if (member == group.createdBy) "(Owner)" else ""}",
                                style = typography.bodyMedium,
                                color = colors.onSurface
                            )
                            Text(
                                when {
                                    balance > 0 -> "Owed $${String.format("%.2f", balance)}"
                                    balance < 0 -> "Owes $${String.format("%.2f", -balance)}"
                                    else -> "Settled up"
                                },
                                style = typography.bodySmall,
                                color = when {
                                    balance > 0 -> colors.primary
                                    balance < 0 -> colors.error
                                    else -> colors.onSurfaceVariant
                                }
                            )
                        }

                        // Show Pay button if current user owes money to this member
                        if (shouldShowPayButton) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    memberToPay = member
                                    amountToPay = balance
                                    showPayDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.primary,
                                    contentColor = colors.onPrimary
                                )
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Pay", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pay", style = typography.labelSmall)
                            }
                        }

                        // Show remove button for owner (except themselves and if not the only member)
                        if (isOwner && member != group.createdBy && group.members.size > 1) {
                            Spacer(modifier = Modifier.width(8.dp))
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isOwner) {
                OutlinedTextField(
                    value = newMemberName,
                    onValueChange = { newMemberName = it },
                    label = { Text("Add Member") },
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
        }
    }

    // Add Expense Dialog with participant selection
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

                    group.members.forEach { member ->
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
                            onAddExpense(expenseDescription, amount, expensePaidBy, selectedParticipants.toList())
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

    // Pay Debt Confirmation Dialog
    if (showPayDialog && memberToPay != null) {
        AlertDialog(
            onDismissRequest = {
                showPayDialog = false
                memberToPay = null
                amountToPay = 0.0
            },
            title = { Text("Confirm Payment") },
            text = {
                Text("Are you sure you want to pay $${String.format("%.2f", amountToPay)} to $memberToPay?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onPayDebt(currentUserEmail, memberToPay!!, amountToPay)
                        showPayDialog = false
                        memberToPay = null
                        amountToPay = 0.0
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                ) {
                    Text("Confirm Payment")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPayDialog = false
                        memberToPay = null
                        amountToPay = 0.0
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
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

// Balance calculation with participant selection
private fun calculateBalancesWithParticipants(group: Group): Map<String, Double> {
    val balances = mutableMapOf<String, Double>()

    group.expenses.forEach { expense ->
        val amountPerPerson = expense.amount / expense.participants.size

        // Person who paid gets money back from the participants
        balances[expense.paidBy] = (balances[expense.paidBy] ?: 0.0) + expense.amount

        // Only the selected participants owe their share
        expense.participants.forEach { participant ->
            balances[participant] = (balances[participant] ?: 0.0) - amountPerPerson
        }
    }

    return balances
}