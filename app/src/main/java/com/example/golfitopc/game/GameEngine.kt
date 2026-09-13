package com.example.golfitopc.game

import com.example.golfitopc.model.Hole
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.pow

object GameEngine {

    private const val HOLE_RADIUS = 0.08f
    private const val FRICTION_DECELERATION = -2.5f

    // Lista de hoyos predefinidos para la progresión de niveles del taller
    val holes = listOf(
        Hole(number = 1, par = 3, startX = 0.15f, startY = 0.50f, targetX = 0.85f, targetY = 0.50f),
        Hole(number = 2, par = 4, startX = 0.20f, startY = 0.20f, targetX = 0.80f, targetY = 0.80f),
        Hole(number = 3, par = 5, startX = 0.50f, startY = 0.85f, targetX = 0.50f, targetY = 0.15f)
    )

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
        val updatedStrokes = state.strokes + 1

        val updatedScores = if (completed) {
            state.holeScores + (state.holeNumber to updatedStrokes)
        } else {
            state.holeScores
        }

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

    // Reinicia el hoyo actual restableciendo la pelota a la posición de salida de ese nivel
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
            holeScores = state.holeScores - state.holeNumber // Limpia la puntuación de este hoyo al reiniciar
        )
    }

    // Avanza al siguiente nivel si está disponible, reiniciando los parámetros de juego correspondientes
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

    fun reset(): GameState {
        return GameState()
    }
}
