package com.example.golfitopc.game

enum class SensorMode {
    TOUCH_AIM_SENSOR_FORCE, // Apuntar arrastrando en pantalla, agitar para dar la fuerza
    FULL_SENSOR             // Usar giroscopio/acelerómetro por completo tanto para dirección como fuerza
}

data class GameState(
    val holeNumber: Int = 1,
    val par: Int = 3,
    val strokes: Int = 0,

    val ballX: Float = 0.50f,
    val ballY: Float = 0.90f,

    val holeX: Float = 0.50f,
    val holeY: Float = 0.10f,

    val holeCompleted: Boolean = false,

    val aimAngleDegrees: Float = 0f,
    val waypoints: List<Pair<Float, Float>> = emptyList(),

    val sensorMode: SensorMode = SensorMode.TOUCH_AIM_SENSOR_FORCE,
    val lastShotForce: Float = 0f,
    val lastShotDirection: Float = 0f,
    val currentHoleIndex: Int = 0,
    val showHomeScreen: Boolean = true,
    val isEnglish: Boolean = true,
    val holeScores: Map<Int, Int> = emptyMap(),
    val currentSensorAcceleration: Float = 0f // Almacena la aceleración del sensor en tiempo real para la barra de fuerza
)
