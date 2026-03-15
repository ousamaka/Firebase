package com.example.firebase.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.firebase.data.model.ChatMessage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    init {
        loadMessages()
    }

    // Escuchar la base de datos en Tiempo Real (Pide el profesor en RA2)
    private fun loadMessages() {
        db.collection("chat")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChatViewModel", "Error escuchando chat", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val chatList = snapshot.documents.mapNotNull { it.toObject(ChatMessage::class.java) }
                    _messages.value = chatList
                }
            }
    }

    // Enviar un mensaje a la nube
    fun sendMessage(text: String, senderName: String = "Usuario Anónimo") {
        if (text.isBlank()) return

        val message = ChatMessage(
            id = db.collection("chat").document().id,
            sender = senderName,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        db.collection("chat").document(message.id).set(message)
    }
}