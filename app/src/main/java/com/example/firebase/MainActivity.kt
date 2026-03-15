package com.example.firebase

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.firebase.presentation.homescreen.HomeViewmodel
import com.example.firebase.ui.theme.FirebaseTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private var isDarkMode by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        setContent {
            FirebaseTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val homeViewModel: HomeViewmodel = androidx.lifecycle.viewmodel.compose.viewModel()
                NavigationWrapper(navController, FirebaseAuth.getInstance(), homeViewModel)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) { isDarkMode = event.values[0] < 10f }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onResume() { super.onResume()
        lightSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }
    override fun onPause() { super.onPause()
        sensorManager.unregisterListener(this)
    }
}