package com.example.firebase.network

import com.example.firebase.data.model.SongResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApiService {
    // Buscamos canciones en iTunes. Ej: search?term=queen&entity=song&limit=20
    @GET("search")
    suspend fun searchSongs(
        @Query("term") query: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 25
    ): SongResponse
}

// Creamos un objeto (Singleton) para usar esta conexión desde cualquier parte
object RetrofitInstance {
    private const val BASE_URL = "https://itunes.apple.com/"

    val api: MusicApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicApiService::class.java)
    }
}