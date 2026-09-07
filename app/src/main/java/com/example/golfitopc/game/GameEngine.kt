package com.example.golfitopc.game

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object GameEngine {

    private const val DISTANCE_SCALE = 0.20f
    private const val HOLE_RADIUS = 0.06f

    fun applyShot(
        state: GameState,
        force: Float,
        directionDegrees: Float
    ): GameState {

        if (state.holeCompleted) {
            return state
        }

        val radians = Math.toRadians(directionDegrees.toDouble())

        val distance = force * DISTANCE_SCALE

        val newX = (
                state.ballX +
                        distance * cos(radians)
                ).toFloat().coerceIn(0f, 1f)

        val newY = (
                state.ballY +
                        distance * sin(radians)
                ).toFloat().coerceIn(0f, 1f)

        val distanceToHole = sqrt(
            (newX - state.holeX) * (newX - state.holeX) +
                    (newY - state.holeY) * (newY - state.holeY)
        )

        val completed = distanceToHole <= HOLE_RADIUS

        return state.copy(
            ballX = newX,
            ballY = newY,
            strokes = state.strokes + 1,
            holeCompleted = completed
        )
    }

    fun reset(): GameState {
        return GameState()
    }
}