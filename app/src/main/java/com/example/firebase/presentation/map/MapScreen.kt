package com.example.firebase.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.firebase.presentation.homescreen.HomeViewmodel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBack: () -> Unit,
    homeViewModel: HomeViewmodel,
    mapViewModel: MapViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val musicTags by mapViewModel.tags.collectAsState()

    var hasLocationPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    // REQUISITO D.1: Notificación de proximidad
    LaunchedEffect(hasLocationPermission, musicTags) {
        if (hasLocationPermission && musicTags.isNotEmpty()) {
            musicTags.forEach { tag ->
                val results = FloatArray(1)
                // Comprobamos distancia (usamos el centro del mapa como 'posición actual' para la demo)
                android.location.Location.distanceBetween(
                    40.4168, -3.7038, // Posición base (Madrid)
                    tag.lat, tag.lng,
                    results
                )
                if (results[0] < 100) { // Si estás a menos de 100 metros
                    Toast.makeText(context, "📍 Estás cerca de una canción: ${tag.title}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.4168, -3.7038), 6f)
    }

    val songs by homeViewModel.songs.collectAsState()
    val currentlyPlayingUrl by homeViewModel.currentlyPlayingUrl.collectAsState()
    val currentSong = songs.find { it.previewUrl == currentlyPlayingUrl }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa Social 🗺️") },
                navigationIcon = {
                    Button(onClick = { onBack() }, modifier = Modifier.padding(start = 8.dp)) {
                        Text("⬅ Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(myLocationButtonEnabled = hasLocationPermission),
                onMapClick = { latLng ->
                    if (currentSong != null) {
                        mapViewModel.addTag(
                            lat = latLng.latitude,
                            lng = latLng.longitude,
                            title = "🎵 ${currentSong.trackName}",
                            snippet = "Por: ${currentSong.artistName} (Toca para borrar)"
                        )
                        Toast.makeText(context, "Marca guardada en la nube ☁️", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Reproduce música primero", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                musicTags.forEach { tag ->
                    MarkerInfoWindow(
                        state = MarkerState(position = LatLng(tag.lat, tag.lng)),
                        title = tag.title,
                        snippet = tag.snippet,
                        onInfoWindowClick = {
                            if (mapViewModel.deleteTag(tag)) {
                                Toast.makeText(context, "Marca eliminada", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Solo puedes borrar tus marcas", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}