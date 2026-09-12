package com.example.golfitopc.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt

@Composable
fun SwingDetector(onSwing: (Float) -> Unit) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        val listener = object : SensorEventListener {
            private var lastUpdate: Long = 0
            private val SWING_THRESHOLD = 12.0f
            private val MAX_SWING = 40.0f

            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val currentTime = System.currentTimeMillis()

                    if (currentTime - lastUpdate > 1000) {
                        val ax = it.values[0]
                        val ay = it.values[1]
                        val az = it.values[2]

                        val magnitude = sqrt((ax * ax + ay * ay + az * az).toDouble()).toFloat()

                        if (magnitude > SWING_THRESHOLD) {
                            lastUpdate = currentTime
                            val normalizedForce = (magnitude / MAX_SWING).coerceIn(0.1f, 1.0f)
                            onSwing(normalizedForce)
                        }
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
}