package com.example.gopayurself.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gopayurself.models.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    group: Group,
    onAddMember: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var newMemberName by remember { mutableStateOf("") }
    var showAddExpenseButton by remember { mutableStateOf(true) }

    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(group.name) },
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
                Text(
                    "• $member",
                    style = typography.bodyLarge,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
}