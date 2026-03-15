package com.example.firebase.presentation.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel : ViewModel() {

    private val _imageBitmap = MutableStateFlow<Bitmap?>(null)
    val imageBitmap: StateFlow<Bitmap?> = _imageBitmap

    private var isLoaded = false
    private val auth = FirebaseAuth.getInstance()

    // Generamos un nombre de archivo único para la foto de este usuario
    private fun getProfileFileName(): String {
        val uid = auth.currentUser?.uid ?: "guest"
        return "profile_pic_$uid.png"
    }

    // Cargamos el archivo físico del usuario actual
    fun loadProfile(context: Context) {
        val file = File(context.filesDir, getProfileFileName())
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            _imageBitmap.value = bitmap
        } else {
            // Si entra un usuario nuevo de Google y no tiene foto, se la quitamos de la pantalla
            _imageBitmap.value = null
        }
        isLoaded = true
    }

    // Guarda la foto de la cámara en su archivo personal
    fun saveCameraImage(context: Context, bitmap: Bitmap) {
        _imageBitmap.value = bitmap
        saveToFile(context, bitmap)
    }

    // Transforma la ruta temporal de la Galería en su archivo permanente
    fun saveGalleryImage(context: Context, uri: Uri) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            _imageBitmap.value = bitmap
            if (bitmap != null) {
                saveToFile(context, bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Función maestra que guarda cualquier foto de forma permanente
    private fun saveToFile(context: Context, bitmap: Bitmap) {
        val file = File(context.filesDir, getProfileFileName())
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // Calidad máxima
        }
    }

    fun logout() {
        auth.signOut()
    }
}