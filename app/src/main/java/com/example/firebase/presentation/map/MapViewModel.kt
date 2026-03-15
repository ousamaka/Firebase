package com.example.firebase.presentation.map

import androidx.lifecycle.ViewModel
import com.example.firebase.data.model.MusicTag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _tags = MutableStateFlow<List<MusicTag>>(emptyList())
    val tags: StateFlow<List<MusicTag>> = _tags

    init {
        loadTagsFromFirebase()
    }

    // Escucha Firebase en tiempo real. Si alguien pone una marca, te sale a ti al instante.
    private fun loadTagsFromFirebase() {
        db.collection("music_tags").addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            if (snapshot != null) {
                val tagList = snapshot.documents.mapNotNull { it.toObject(MusicTag::class.java) }
                _tags.value = tagList
            }
        }
    }

    // Sube tu marca a la nube
    fun addTag(lat: Double, lng: Double, title: String, snippet: String) {
        val newDoc = db.collection("music_tags").document()
        val tag = MusicTag(
            id = newDoc.id,
            lat = lat,
            lng = lng,
            title = title,
            snippet = snippet,
            userEmail = auth.currentUser?.email ?: "Anónimo"
        )
        newDoc.set(tag)
    }

    // Borra una marca (solo si es tuya)
    fun deleteTag(tag: MusicTag): Boolean {
        val currentUserEmail = auth.currentUser?.email
        if (currentUserEmail != null && currentUserEmail == tag.userEmail) {
            db.collection("music_tags").document(tag.id).delete()
            return true
        }
        return false // Devuelve falso si intentas borrar la de otro
    }
}