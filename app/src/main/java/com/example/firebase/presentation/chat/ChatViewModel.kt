package com.example.firebase.presentation.chat

import androidx.lifecycle.ViewModel
import com.example.firebase.data.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    init {
        // Leemos de la base de datos de Firebase en tiempo real
        db.collection("chat_messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val msgList = snapshot.documents.mapNotNull { it.toObject(ChatMessage::class.java) }
                    _messages.value = msgList
                }
            }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val email = auth.currentUser?.email ?: "Usuario Local"
        val message = ChatMessage(
            sender = email,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        // Guardamos en la nube para que no se borre al cerrar la app
        db.collection("chat_messages").add(message)
    }
}