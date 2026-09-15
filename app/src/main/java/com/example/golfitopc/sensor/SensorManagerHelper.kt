package com.example.golfitopc.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.atan2
import kotlin.math.sqrt

class SensorManagerHelper(
    context: Context,
    private val onShotDetected: (force: Float, directionDegrees: Float) -> Unit
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastShotTime = 0L

    // Evita que pequeñas vibraciones generen golpes
    private val movementThreshold = 2.5f

    // Evita detectar varios golpes seguidos
    private val shotCooldown = 1000L

    // Callback dinámico opcional para actualizar el palito de dirección en tiempo real
    var onDirectionChanged: ((Float) -> Unit)? = null

    // Callback dinámico opcional para actualizar la barra de fuerza en tiempo real en la pantalla
    var onAccelerationChanged: ((Float) -> Unit)? = null

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) {
            return
        }

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Calculamos la dirección del palito continuamente inclinando el celular sin necesidad de tirar fuerte
        val currentDirection = Math.toDegrees(atan2(y.toDouble(), x.toDouble())).toFloat()
        onDirectionChanged?.invoke(currentDirection)

        // Magnitud de la aceleración
        val acceleration = sqrt(
            x * x +
                    y * y +
                    z * z
        )

        // Eliminamos aproximadamente la gravedad
        val movement = kotlin.math.abs(acceleration - SensorManager.GRAVITY_EARTH)

        // Enviamos continuamente el porcentaje de movimiento para la barra de fuerza (de 0.0f a 1.0f)
        val realtimePowerPercentage = (movement / 15f).coerceIn(0.0f, 1.0f)
        onAccelerationChanged?.invoke(realtimePowerPercentage)

        // Si el movimiento es pequeño, no hacemos nada
        if (movement < movementThreshold) {
            return
        }

        val currentTime = System.currentTimeMillis()

        // Evita múltiples golpes por un mismo movimiento
        if (currentTime - lastShotTime < shotCooldown) {
            return
        }

        lastShotTime = currentTime

        // Convertimos el movimiento en una fuerza entre 0 y 1, haciéndola más sensible multiplicando por 2.5f para imitar un palo real
        val force = ((movement / 15f) * 2.5f).coerceIn(0.1f, 1.0f)

        onShotDetected(force, currentDirection)
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
        // No necesitamos hacer nada aquí
    }
}