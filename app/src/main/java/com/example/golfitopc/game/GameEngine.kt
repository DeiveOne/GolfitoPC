package com.example.golfitopc.game

import com.example.golfitopc.model.Hole
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.pow

// 1. Modelo de datos para los obstáculos
data class Obstacle(val x: Float, val y: Float, val width: Float, val height: Float)

// 2. Generador de niveles (Compartido entre la UI y el Motor de Físicas)
fun getObstaclesForLevel(level: Int): List<Obstacle> {
    return when (level) {
        1 -> listOf(
            Obstacle(x = 0.0f, y = 0.35f, width = 0.65f, height = 0.08f),
            Obstacle(x = 0.35f, y = 0.65f, width = 0.65f, height = 0.08f)
        )
        2 -> listOf(
            Obstacle(x = 0.45f, y = 0.2f, width = 0.1f, height = 0.8f),
            Obstacle(x = 0.3f, y = 0.2f, width = 0.25f, height = 0.08f),
            Obstacle(x = 0.55f, y = 0.2f, width = 0.25f, height = 0.08f)
        )

        3 -> listOf(
            Obstacle(x = 0.0f, y = 0.10f, width = 0.55f, height = 0.05f),
            Obstacle(x = 0.75f, y = 0.10f, width = 0.45f, height = 0.05f),
            Obstacle(x = 0.0f, y = 0.35f, width = 0.25f, height = 0.05f),
            Obstacle(x = 0.45f, y = 0.35f, width = 1.0f, height = 0.05f),
            Obstacle(x = 0.0f, y = 0.65f, width = 0.75f, height = 0.05f),
            Obstacle(x = 0.95f, y = 0.65f, width = 0.25f, height = 0.05f)

        )
        else -> emptyList()
    }
}

object GameEngine {

    private const val HOLE_RADIUS = 0.08f
    private const val FRICTION_DECELERATION = -2.5f

    val holes = listOf(
        Hole(number = 1, par = 3, startX = 0.50f, startY = 0.90f, targetX = 0.50f, targetY = 0.10f),
        Hole(number = 2, par = 4, startX = 0.15f, startY = 0.90f, targetX = 0.85f, targetY = 0.85f),
        Hole(number = 3, par = 5, startX = 0.50f, startY = 0.90f, targetX = 0.10f, targetY = 0.05f)
    )

    fun applyShot(
        state: GameState,
        force: Float,
        directionDegrees: Float
    ): GameState {

        if (state.holeCompleted) return state

        val radians = Math.toRadians(directionDegrees.toDouble())
        val initialVelocity = force * 1.5f
        var remainingDistance = -(initialVelocity.pow(2)) / (2 * FRICTION_DECELERATION)

        var currentX = state.ballX
        var currentY = state.ballY

        var dirX = cos(radians).toFloat()
        var dirY = sin(radians).toFloat()

        val waypoints = mutableListOf<Pair<Float, Float>>()
        val obstacles = getObstaclesForLevel(state.holeNumber)

        while (remainingDistance > 0.0001f) {
            var closestDist = Float.MAX_VALUE
            var bounceX = false
            var bounceY = false

            // A. Colisiones con los bordes del mapa
            val screenDistX = if (dirX > 0) (1f - currentX) / dirX else if (dirX < 0) (0f - currentX) / dirX else Float.MAX_VALUE
            val screenDistY = if (dirY > 0) (1f - currentY) / dirY else if (dirY < 0) (0f - currentY) / dirY else Float.MAX_VALUE

            if (screenDistX < closestDist) { closestDist = screenDistX; bounceX = true; bounceY = false }
            if (screenDistY < closestDist) { closestDist = screenDistY; bounceX = false; bounceY = true }
            if (screenDistX == screenDistY) { closestDist = screenDistX; bounceX = true; bounceY = true }

            // B. Colisiones Raycasting con los muros
            for (obs in obstacles) {
                val minX = obs.x
                val maxX = obs.x + obs.width
                val minY = obs.y
                val maxY = obs.y + obs.height

                // Eje X (Paredes laterales del obstáculo)
                if (dirX > 0 && currentX <= minX) {
                    val t = (minX - currentX) / dirX
                    val yAtHit = currentY + t * dirY
                    if (t > 0.001f && t < closestDist && yAtHit in minY..maxY) {
                        closestDist = t; bounceX = true; bounceY = false
                    }
                } else if (dirX < 0 && currentX >= maxX) {
                    val t = (maxX - currentX) / dirX
                    val yAtHit = currentY + t * dirY
                    if (t > 0.001f && t < closestDist && yAtHit in minY..maxY) {
                        closestDist = t; bounceX = true; bounceY = false
                    }
                }

                // Eje Y (Paredes superior/inferior del obstáculo)
                if (dirY > 0 && currentY <= minY) {
                    val t = (minY - currentY) / dirY
                    val xAtHit = currentX + t * dirX
                    if (t > 0.001f && t < closestDist && xAtHit in minX..maxX) {
                        closestDist = t; bounceX = false; bounceY = true
                    }
                } else if (dirY < 0 && currentY >= maxY) {
                    val t = (maxY - currentY) / dirY
                    val xAtHit = currentX + t * dirX
                    if (t > 0.001f && t < closestDist && xAtHit in minX..maxX) {
                        closestDist = t; bounceX = false; bounceY = true
                    }
                }
            }

            // C. Resolver movimiento y rebote
            val stepDist = if (closestDist >= remainingDistance) remainingDistance else closestDist

            // Verificar matemáticamente si en este tramo rectilíneo la pelota toca o cruza el hoyo
            val tHole = (state.holeX - currentX) * dirX + (state.holeY - currentY) * dirY
            val tHoleClamped = tHole.coerceIn(0f, stepDist)
            val checkX = currentX + dirX * tHoleClamped
            val checkY = currentY + dirY * tHoleClamped
            val dToHole = sqrt((checkX - state.holeX).pow(2) + (checkY - state.holeY).pow(2))

            if (dToHole <= HOLE_RADIUS) {
                // Si cruza el rango del hoyo, cae inmediatamente dentro y termina su recorrido en el centro
                currentX = state.holeX
                currentY = state.holeY
                waypoints.add(Pair(currentX, currentY))
                break
            }

            if (closestDist >= remainingDistance) {
                currentX += dirX * remainingDistance
                currentY += dirY * remainingDistance
                waypoints.add(Pair(currentX, currentY))
                break
            } else {
                currentX += dirX * closestDist
                currentY += dirY * closestDist
                waypoints.add(Pair(currentX, currentY))
                remainingDistance -= closestDist

                if (bounceX) dirX = -dirX
                if (bounceY) dirY = -dirY
            }
        }

        val distanceToHole = sqrt((currentX - state.holeX).pow(2) + (currentY - state.holeY).pow(2))
        val completed = distanceToHole <= HOLE_RADIUS
        val updatedStrokes = state.strokes + 1
        val updatedScores = if (completed) state.holeScores + (state.holeNumber to updatedStrokes) else state.holeScores

        return state.copy(
            ballX = currentX,
            ballY = currentY,
            strokes = updatedStrokes,
            holeCompleted = completed,
            waypoints = waypoints,
            lastShotForce = force,
            lastShotDirection = directionDegrees,
            holeScores = updatedScores
        )
    }

    fun resetHole(state: GameState): GameState {
        val currentHole = holes.getOrNull(state.currentHoleIndex) ?: holes[0]
        return state.copy(
            strokes = 0,
            ballX = currentHole.startX,
            ballY = currentHole.startY,
            holeCompleted = false,
            waypoints = emptyList(),
            lastShotForce = 0f,
            lastShotDirection = 0f,
            holeScores = state.holeScores - state.holeNumber
        )
    }

    fun nextHole(state: GameState): GameState {
        val nextIndex = (state.currentHoleIndex + 1) % holes.size
        val nextHole = holes[nextIndex]
        return state.copy(
            holeNumber = nextHole.number,
            par = nextHole.par,
            strokes = 0,
            ballX = nextHole.startX,
            ballY = nextHole.startY,
            holeX = nextHole.targetX,
            holeY = nextHole.targetY,
            holeCompleted = false,
            waypoints = emptyList(),
            lastShotForce = 0f,
            lastShotDirection = 0f,
            currentHoleIndex = nextIndex
        )
    }

}