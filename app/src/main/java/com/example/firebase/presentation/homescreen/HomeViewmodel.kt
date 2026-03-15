package com.example.firebase.presentation.homescreen

import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebase.data.model.Song
import com.example.firebase.network.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewmodel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentlyPlayingUrl = MutableStateFlow<String?>(null)
    val currentlyPlayingUrl: StateFlow<String?> = _currentlyPlayingUrl

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds

    private var mediaPlayer: MediaPlayer? = null
    
    // Usamos un nombre de archivo que incluya el UID para que sea independiente por usuario
    private fun getPrefsName(): String {
        val uid = auth.currentUser?.uid ?: "guest"
        return "music_prefs_$uid"
    }

    init {
        loadFavorites()
        searchMusic("Pop")
    }

    fun loadFavorites() {
        val sharedPrefs = getApplication<Application>().getSharedPreferences(getPrefsName(), Context.MODE_PRIVATE)
        val favs = sharedPrefs.getStringSet("favorites", emptySet()) ?: emptySet()
        _favoriteIds.value = favs
    }

    fun toggleFavorite(trackId: String) {
        val sharedPrefs = getApplication<Application>().getSharedPreferences(getPrefsName(), Context.MODE_PRIVATE)
        val currentFavs = _favoriteIds.value.toMutableSet()
        if (currentFavs.contains(trackId)) currentFavs.remove(trackId)
        else currentFavs.add(trackId)
        _favoriteIds.value = currentFavs
        sharedPrefs.edit().putStringSet("favorites", currentFavs).apply()
    }

    fun searchMusic(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.searchSongs(query)
                _songs.value = response.results
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun recommendRandom() {
        searchMusic(listOf("Rock", "Techno", "Pop", "Jazz").random())
    }

    fun playAudio(url: String?) {
        if (url == null) return
        if (_currentlyPlayingUrl.value == url) {
            mediaPlayer?.stop()
            _currentlyPlayingUrl.value = null
            return
        }
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                start()
                _currentlyPlayingUrl.value = url
            }
            setOnCompletionListener { _currentlyPlayingUrl.value = null }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }
}