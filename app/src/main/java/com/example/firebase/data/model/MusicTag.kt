package com.example.firebase.data.model

data class MusicTag(
    val id: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val title: String = "",
    val snippet: String = "",
    val userEmail: String = "" // Guardamos quién la puso para que solo él pueda borrarla
)