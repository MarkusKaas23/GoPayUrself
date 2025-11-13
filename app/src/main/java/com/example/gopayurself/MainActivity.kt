package com.example.gopayurself

import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gopayurself.ui.theme.GoPayUrselfTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoPayUrselfTheme {
                Surface( // ensures background matches theme
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GroupScreen()
                }
            }
        }
    }
}

@Composable
fun GroupScreen(viewModel: GroupViewModel = viewModel()) {
    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Finance Group",
            style = typography.headlineMedium.copy(color = colors.onBackground)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Members:",
            style = typography.titleMedium.copy(color = colors.onSurface)
        )

        Spacer(modifier = Modifier.height(8.dp))

        viewModel.members.forEach { name ->
            Text(
                "• $name",
                style = typography.bodyLarge.copy(color = colors.onSurfaceVariant),
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = viewModel.newMemberName,
            onValueChange = viewModel::onNewMemberNameChange,
            label = { Text("Enter name", style = typography.labelLarge) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.outline,
                focusedLabelColor = colors.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = viewModel::addMember,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            )
        ) {
            Text("Add Person", style = typography.labelLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupScreenPreview() {
    GoPayUrselfTheme {
        GroupScreen(viewModel = GroupViewModel())
    }
}
