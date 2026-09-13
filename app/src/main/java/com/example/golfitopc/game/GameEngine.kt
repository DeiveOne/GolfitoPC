package com.example.golfitopc.game

import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.pow

object GameEngine {

    private const val HOLE_RADIUS = 0.06f
    private const val FRICTION_DECELERATION = -2.5f

    fun applyShot(
        state: GameState,
        force: Float,
        directionDegrees: Float
    ): GameState {

        if (state.holeCompleted) {
            return state
        }

        val radians = Math.toRadians(directionDegrees.toDouble())
        val initialVelocity = force * 1.5f
        var remainingDistance = -(initialVelocity.pow(2)) / (2 * FRICTION_DECELERATION)

        var currentX = state.ballX
        var currentY = state.ballY

        // Vectores directores (hacia dónde avanza la pelota)
        var dirX = cos(radians).toFloat()
        var dirY = sin(radians).toFloat()

        val waypoints = mutableListOf<Pair<Float, Float>>()

        // Cálculo de colisiones y rebotes usando raycasting
        while (remainingDistance > 0.0001f) {
            val distX = if (dirX > 0) (1f - currentX) / dirX else if (dirX < 0) (0f - currentX) / dirX else Float.MAX_VALUE
            val distY = if (dirY > 0) (1f - currentY) / dirY else if (dirY < 0) (0f - currentY) / dirY else Float.MAX_VALUE

            val distanceToNextWall = min(distX, distY)

            if (distanceToNextWall >= remainingDistance) {
                currentX += dirX * remainingDistance
                currentY += dirY * remainingDistance
                waypoints.add(Pair(currentX, currentY))
                break
            } else {
                currentX += dirX * distanceToNextWall
                currentY += dirY * distanceToNextWall
                waypoints.add(Pair(currentX, currentY))

                remainingDistance -= distanceToNextWall

                if (distanceToNextWall == distX) {
                    dirX = -dirX
                    currentX = if (dirX > 0) 0f else 1f
                }
                if (distanceToNextWall == distY) {
                    dirY = -dirY
                    currentY = if (dirY > 0) 0f else 1f
                }
            }
        }

        val distanceToHole = sqrt(
            (currentX - state.holeX).pow(2) + (currentY - state.holeY).pow(2)
        )

        val completed = distanceToHole <= HOLE_RADIUS

        return state.copy(
            ballX = currentX,
            ballY = currentY,
            strokes = state.strokes + 1,
            holeCompleted = completed,
            waypoints = waypoints
        )
    }

    fun reset(): GameState {
        return GameState()
    }
}