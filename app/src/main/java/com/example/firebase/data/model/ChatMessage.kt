package com.example.firebase.data.model

data class ChatMessage(
    val id: String = "",
    val sender: String = "Usuario",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)