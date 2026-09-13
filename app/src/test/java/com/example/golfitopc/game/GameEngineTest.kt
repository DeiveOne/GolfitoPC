package com.example.golfitopc.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {

    @Test
    fun testApplyShotIncrementsStrokes() {
        val initialState = GameState()
        assertEquals(0, initialState.strokes)

        val updatedState = GameEngine.applyShot(initialState, force = 0.5f, directionDegrees = 0f)
        assertEquals(1, updatedState.strokes)
        assertEquals(0.5f, updatedState.lastShotForce)
        assertEquals(0f, updatedState.lastShotDirection)
    }

    @Test
    fun testHoleValidationWhenClose() {
        // Ponemos la pelota extremadamente cerca del hoyo para asegurar que entre
        val nearHoleState = GameState(
            ballX = 0.84f,
            ballY = 0.50f,
            holeX = 0.85f,
            holeY = 0.50f,
            holeCompleted = false
        )

        // Hacemos un golpe pequeño hacia el hoyo
        val nextState = GameEngine.applyShot(nearHoleState, force = 0.1f, directionDegrees = 0f)
        assertTrue(nextState.holeCompleted)
    }

    @Test
    fun testResetHoleClearsStrokesAndKeepsIndex() {
        val shotState = GameState(strokes = 4, currentHoleIndex = 1, holeCompleted = true)
        val resetState = GameEngine.resetHole(shotState)

        assertEquals(0, resetState.strokes)
        assertEquals(1, resetState.currentHoleIndex)
        assertFalse(resetState.holeCompleted)
    }

    @Test
    fun testNextHoleProgressesCorrectly() {
        val stateHole1 = GameState(currentHoleIndex = 0)
        val stateHole2 = GameEngine.nextHole(stateHole1)

        assertEquals(1, stateHole2.currentHoleIndex)
        assertEquals(2, stateHole2.holeNumber)
        assertEquals(0, stateHole2.strokes)
    }
}
