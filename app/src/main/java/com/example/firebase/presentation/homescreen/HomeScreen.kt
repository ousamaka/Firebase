package com.example.firebase.presentation.homescreen

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.firebase.data.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewmodel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navigateToProfile: () -> Unit,
    navigateToChat: () -> Unit,
    navigateToMap: () -> Unit
) {
    val songs by viewModel.songs.collectAsState()
    val currentlyPlayingUrl by viewModel.currentlyPlayingUrl.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]; val y = event.values[1]; val z = event.values[2]
                val speed = Math.abs(x + y + z)
                if (speed > 30) viewModel.recommendRandom()
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SoundConnect", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = navigateToMap) { Icon(Icons.Filled.Map, null) }
                    IconButton(onClick = navigateToChat) { Icon(Icons.Filled.Chat, null) }
                    IconButton(onClick = navigateToProfile) { Icon(Icons.Filled.Person, null) }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar...") },
                trailingIcon = { IconButton(onClick = { viewModel.searchMusic(searchQuery) }) { Icon(Icons.Filled.Search, null) } }
            )
            if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(songs) { song ->
                    val isFav = favoriteIds.contains(song.trackId.toString())
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { 
                            song.previewUrl?.let { viewModel.playAudio(it) } 
                        },
                        colors = CardDefaults.cardColors(containerColor = if (currentlyPlayingUrl == song.previewUrl) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = song.artworkUrl, // Corregido de .artwork a .artworkUrl
                                contentDescription = null, 
                                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp))
                            )
                            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                                Text(text = song.trackName, fontWeight = FontWeight.Bold)
                                Text(text = song.artistName, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { viewModel.toggleFavorite(song.trackId.toString()) }) {
                                Icon(if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, null, tint = if (isFav) Color.Red else Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}