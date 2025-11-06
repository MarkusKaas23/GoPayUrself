package com.example.gopayurself

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class GroupViewModel : ViewModel() {
    var newMemberName by mutableStateOf("")
        private set

    var members by mutableStateOf(listOf<String>())
        private set

    fun onNewMemberNameChange(name: String) {
        newMemberName = name
    }

    fun addMember() {
        if (newMemberName.isNotBlank()) {
            members = members + newMemberName.trim()
            newMemberName = ""
        }
    }
}
