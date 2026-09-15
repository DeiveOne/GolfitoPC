package com.example.golfitopc

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.golfitopc.game.GameEngine
import com.example.golfitopc.game.GameState
import com.example.golfitopc.game.SensorMode
import com.example.golfitopc.sensor.SensorManagerHelper
import com.example.golfitopc.ui.components.SwingDetector
import com.example.golfitopc.ui.screens.GameScreen
import com.example.golfitopc.ui.theme.GolfitoPCTheme

class MainActivity : ComponentActivity() {

    private var toneGenerator: ToneGenerator? = null

    private fun playToneSound(toneType: Int, durationMs: Int) {
        try {
            if (toneGenerator == null) {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
            }
            toneGenerator?.startTone(toneType, durationMs)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun triggerVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = getSystemService(VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(150)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            GolfitoPCTheme {

                val gameState = remember {
                    mutableStateOf(GameState())
                }

                // 1. Instanciamos el detector de movimiento físico (Modo: Apuntar con dedo + Fuerza con sensor)
                SwingDetector(
                    onSwing = { calculatedForce ->
                        // Condición estricta: Solo registrar swing si no está en la Home Screen
                        if (!gameState.value.showHomeScreen && !gameState.value.holeCompleted && gameState.value.sensorMode == SensorMode.TOUCH_AIM_SENSOR_FORCE) {
                            triggerVibration()
                            val oldCompleted = gameState.value.holeCompleted
                            val newState = GameEngine.applyShot(
                                state = gameState.value,
                                force = calculatedForce,
                                directionDegrees = gameState.value.aimAngleDegrees
                            )
                            gameState.value = newState
                            
                            // Reproducir sonidos dinámicos nativos
                            if (newState.holeCompleted && !oldCompleted) {
                                playToneSound(ToneGenerator.TONE_DTMF_A, 350) // Tono de celebración / Hoyo completado
                            } else {
                                playToneSound(ToneGenerator.TONE_PROP_BEEP, 120) // Pitido estándar de golpe / rebote
                            }
                        }
                    }
                )

                // 2. Instanciamos el manejador de sensor completo (Modo: Dirección y Fuerza con sensores)
                val sensorHelper = remember {
                    SensorManagerHelper(this) { force, direction ->
                        // Condición estricta: Solo registrar disparo si no está en la Home Screen
                        if (!gameState.value.showHomeScreen && !gameState.value.holeCompleted && gameState.value.sensorMode == SensorMode.FULL_SENSOR) {
                            triggerVibration()
                            val oldCompleted = gameState.value.holeCompleted
                            val newState = GameEngine.applyShot(
                                state = gameState.value,
                                force = force,
                                directionDegrees = direction
                            )
                            gameState.value = newState

                            if (newState.holeCompleted && !oldCompleted) {
                                playToneSound(ToneGenerator.TONE_DTMF_A, 350)
                            } else {
                                playToneSound(ToneGenerator.TONE_PROP_BEEP, 120)
                            }
                        }
                    }.apply {
                        // Conectamos la actualización continua del palito de dirección mediante el sensor en tiempo real
                        onDirectionChanged = { direction ->
                            if (!gameState.value.showHomeScreen && gameState.value.sensorMode == SensorMode.FULL_SENSOR && !gameState.value.holeCompleted) {
                                gameState.value = gameState.value.copy(aimAngleDegrees = direction)
                            }
                        }
                        
                        // Conectamos la barra de potencia visual en tiempo real mapeando la aceleración física del sensor
                        onAccelerationChanged = { currentPower ->
                            if (!gameState.value.showHomeScreen && !gameState.value.holeCompleted) {
                                gameState.value = gameState.value.copy(currentSensorAcceleration = currentPower)
                            }
                        }
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

                    onAim = { newAngle ->
                        // Permitir cambiar ángulo arrastrando solo en modo TOUCH_AIM_SENSOR_FORCE
                        if (gameState.value.sensorMode == SensorMode.TOUCH_AIM_SENSOR_FORCE) {
                            gameState.value = gameState.value.copy(aimAngleDegrees = newAngle)
                        }
                    },

                    onReset = {
                        gameState.value = GameEngine.resetHole(gameState.value)
                    },

                    onNextHole = {
                        gameState.value = GameEngine.nextHole(gameState.value)
                    },

                    onChangeMode = { selectedMode ->
                        gameState.value = gameState.value.copy(sensorMode = selectedMode)
                    },

                    onStartGame = {
                        gameState.value = gameState.value.copy(showHomeScreen = false)
                    },

                    onExitGame = {
                        gameState.value = gameState.value.copy(showHomeScreen = true)
                    },

                    onToggleLanguage = {
                        gameState.value = gameState.value.copy(isEnglish = !gameState.value.isEnglish)
                    }
                )
            }
        }
    }
}
