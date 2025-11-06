package com.example.gopayurself

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gopayurself.ui.theme.GoPayUrselfTheme
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoPayUrselfTheme {
                GroupScreen()
            }
        }
    }
}

@Composable
fun GroupScreen(viewModel: GroupViewModel = viewModel()) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Finance Group", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Members:", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))

        viewModel.members.forEach { name ->
            Text("• $name", modifier = Modifier.padding(vertical = 4.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = viewModel.newMemberName,
            onValueChange = viewModel::onNewMemberNameChange,
            label = { Text("Enter name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = viewModel::addMember,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Person")
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


