package com.example.golfitopc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.golfitopc.game.GameEngine
import com.example.golfitopc.game.GameState
import com.example.golfitopc.sensor.SensorManagerHelper
import com.example.golfitopc.ui.GameScreen
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

                val sensorHelper = remember {
                    SensorManagerHelper(this) { force, direction ->

                        gameState.value = GameEngine.applyShot(
                            state = gameState.value,
                            force = force,
                            directionDegrees = direction
                        )
                    }
                }

                DisposableEffect(Unit) {

                    sensorHelper.start()

                    onDispose {
                        sensorHelper.stop()
                    }
                }

                GameScreen(
                    state = gameState.value,

                    onShot = {
                        // Golpe de prueba
                        gameState.value = GameEngine.applyShot(
                            state = gameState.value,
                            force = 0.8f,
                            directionDegrees = 0f
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