package com.example.firebase.data.model

data class ChatMessage(
    val sender: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)