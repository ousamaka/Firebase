package com.example.firebase.presentation.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel : ViewModel() {

    private val _imageBitmap = MutableStateFlow<Bitmap?>(null)
    val imageBitmap: StateFlow<Bitmap?> = _imageBitmap

    private var isLoaded = false

    // Cargamos el archivo físico desde la memoria interna de la app
    fun loadProfile(context: Context) {
        if (isLoaded) return
        val file = File(context.filesDir, "profile_pic.png")
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            _imageBitmap.value = bitmap
        }
        isLoaded = true
    }

    // Guarda la foto de la cámara en un archivo local
    fun saveCameraImage(context: Context, bitmap: Bitmap) {
        _imageBitmap.value = bitmap
        saveToFile(context, bitmap)
    }

    // Transforma la ruta temporal de la Galería en un archivo local permanente
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
        val file = File(context.filesDir, "profile_pic.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // Calidad máxima
        }
    }
}