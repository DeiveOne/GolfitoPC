package com.example.golfitopc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.golfitopc.game.GameEngine
import com.example.golfitopc.game.GameState
import com.example.golfitopc.ui.GameScreen
import com.example.golfitopc.ui.SwingDetector
import com.example.golfitopc.ui.theme.GolfitoPCTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            GolfitoPCTheme {

                val gameState = remember {
                    mutableStateOf(GameState())
                }

                // 1. Instanciamos el detector de movimiento físico
                SwingDetector(
                    onSwing = { calculatedForce ->
                        if (!gameState.value.holeCompleted) {
                            gameState.value = GameEngine.applyShot(
                                state = gameState.value,
                                force = calculatedForce,
                                directionDegrees = gameState.value.aimAngleDegrees // Usa el ángulo táctil
                            )
                        }
                    }
                )

                // 2. Instanciamos la interfaz gráfica
                GameScreen(
                    state = gameState.value,

                    onAim = { newAngle ->
                        gameState.value = gameState.value.copy(aimAngleDegrees = newAngle)
                    },

                    onShot = {
                        gameState.value = GameEngine.applyShot(
                            state = gameState.value,
                            force = 0.5f, // Fuerza predeterminada para el botón de prueba
                            directionDegrees = gameState.value.aimAngleDegrees
                        )
                    },

                    onReset = {
                        gameState.value = GameEngine.reset()
                    }
                )
            }
        }
    }
}