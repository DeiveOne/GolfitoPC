package com.example.golfitopc.game

data class GameState(
    val holeNumber: Int = 1,
    val par: Int = 3,
    val strokes: Int = 0,

    val ballX: Float = 0.15f,
    val ballY: Float = 0.50f,

    val holeX: Float = 0.85f,
    val holeY: Float = 0.50f,

    val holeCompleted: Boolean = false,

    val aimAngleDegrees: Float = 0f,
    val waypoints: List<Pair<Float, Float>> = emptyList()
)