package com.example.firebase.data.model

import com.google.gson.annotations.SerializedName

// Esta clase representa la respuesta completa de la API
data class SongResponse(
    @SerializedName("results") val results: List<Song>
)

// Esta clase representa cada canción individual
data class Song(
    @SerializedName("trackId") val trackId: Int,
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("artworkUrl100") val artworkUrl: String, // Carátula del disco
    @SerializedName("previewUrl") val previewUrl: String? // Fragmento de audio
)