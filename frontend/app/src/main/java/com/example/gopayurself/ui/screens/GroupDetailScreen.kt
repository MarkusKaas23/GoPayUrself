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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gopayurself.api.models.GroupApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    group: GroupApi,
    expenses: List<com.example.gopayurself.api.models.ExpenseApi>,
    currentUserEmail: String,
    onAddMember: (String) -> Unit,
    onRemoveMember: (String) -> Unit,
    onDeleteGroup: () -> Unit,
    onAddExpense: (String, Double, String, List<String>) -> Unit,
    onPayDebt: (String, String, Double) -> Unit,
    onDeleteExpense: (String) -> Unit,
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
    var payerDropdownExpanded by remember { mutableStateOf(false) }
    var selectedParticipants by remember { mutableStateOf<Set<String>>(emptySet()) }

    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme

    val isOwner = group.owner.email == currentUserEmail

    // Calculate balances with participant selection
    val memberBalances = calculateBalancesWithParticipants(group, expenses)
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
                        "$${String.format("%.2f", expenses.sumOf { it.amount })}",
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

            // Expenses Section - TODO: Fetch and display expenses
            Text(
                "Expenses:",
                style = typography.titleMedium,
                color = colors.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (expenses.isEmpty()) {
                Text(
                    "No expenses yet",
                    style = typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            } else {
                expenses.forEach { expense ->
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            expense.payer.firstName + " " + expense.payer.lastName,
                                            style = typography.bodyMedium,
                                            color = colors.onSurface
                                        )
                                        Text(
                                            "$${String.format("%.2f", expense.amount)}",
                                            style = typography.bodyLarge,
                                            color = colors.primary
                                        )
                                    }
                                    Text(
                                        "Paid for: ${expense.splits.joinToString(", ") { "${it.user.firstName} ${it.user.lastName}" }}",
                                        style = typography.bodySmall,
                                        color = colors.onSurfaceVariant
                                    )
                                    Text(
                                        "Date: ${expense.date.substring(0, 10)}", // Show date part only
                                        style = typography.bodySmall,
                                        color = colors.onSurfaceVariant
                                    )
                                }
                                if (expense.payer.email == currentUserEmail) {
                                    IconButton(
                                        onClick = { onDeleteExpense(expense.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete Expense",
                                            tint = colors.error
                                        )
                                    }
                                }
                            }
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

            val allUsers = listOf(group.owner) + group.members.map { it.user }
            // Show balances for each user with Pay buttons
            allUsers.forEach { user ->
                val balance = memberBalances[user.email] ?: 0.0
                // Show Pay button if current user owes money to this user
                val shouldShowPayButton = balance > 0 && user.email != currentUserEmail

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            user.email == currentUserEmail -> colors.primaryContainer.copy(alpha = 0.3f)
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
                                "${user.firstName} ${user.lastName} ${if (user.id == group.ownerId) "(Owner)" else ""}",
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

                        // Show Pay button if current user owes money to this user
                        if (shouldShowPayButton) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    memberToPay = "${user.firstName} ${user.lastName}"
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
                        if (isOwner && user.email != group.owner.email && group.members.size > 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    selectedMember = "${user.firstName} ${user.lastName}"
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

                    val allUsers = listOf(group.owner) + group.members.map { it.user }
                    ExposedDropdownMenuBox(
                        expanded = payerDropdownExpanded,
                        onExpandedChange = { payerDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = expensePaidBy,
                            onValueChange = {},
                            label = { Text("Who paid?") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = payerDropdownExpanded) }
                        )
                        ExposedDropdownMenu(
                            expanded = payerDropdownExpanded,
                            onDismissRequest = { payerDropdownExpanded = false }
                        ) {
                            allUsers.forEach { user ->
                                val userName = "${user.firstName} ${user.lastName}"
                                DropdownMenuItem(
                                    text = { Text(userName) },
                                    onClick = {
                                        expensePaidBy = userName
                                        payerDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Who should split this expense?", style = typography.labelMedium)

                    allUsers.forEach { user ->
                        val userName = "${user.firstName} ${user.lastName}"
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = selectedParticipants.contains(userName),
                                onCheckedChange = { checked ->
                                    selectedParticipants = if (checked) {
                                        selectedParticipants + userName
                                    } else {
                                        selectedParticipants - userName
                                    }
                                }
                            )
                            Text(
                                text = userName,
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
                            payerDropdownExpanded = false
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
                        payerDropdownExpanded = false
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
private fun calculateBalancesWithParticipants(group: com.example.gopayurself.api.models.GroupApi, expenses: List<com.example.gopayurself.api.models.ExpenseApi>): Map<String, Double> {
    val allUsers = listOf(group.owner) + group.members.map { it.user }
    val balances = mutableMapOf<String, Double>()

    // Initialize balances to 0
    allUsers.forEach { balances[it.email] = 0.0 }

    // Calculate balances from expenses
    expenses.forEach { expense ->
        val payerEmail = expense.payer.email
        val totalAmount = expense.amount
        val numSplits = expense.splits.size
        val splitAmount = if (numSplits > 0) totalAmount / numSplits else 0.0

        // Payer gets credit for the full amount
        balances[payerEmail] = (balances[payerEmail] ?: 0.0) + totalAmount

        // Each participant owes their share
        expense.splits.forEach { split ->
            val participantEmail = split.user.email
            balances[participantEmail] = (balances[participantEmail] ?: 0.0) - split.amount
        }
    }

    return balances
}