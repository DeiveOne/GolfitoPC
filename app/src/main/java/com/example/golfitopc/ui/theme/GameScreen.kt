package com.example.golfitopc.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.golfitopc.game.GameState
import com.example.golfitopc.game.SensorMode
import com.example.golfitopc.game.getObstaclesForLevel // <- Importante: Lee los datos del motor
import com.example.golfitopc.ui.theme.GolfBallWhite
import com.example.golfitopc.ui.theme.GolfCourseGreen
import com.example.golfitopc.ui.theme.GolfHoleBlack
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun GameScreen(
    state: GameState,
    onAim: (Float) -> Unit,
    onReset: () -> Unit,
    onNextHole: () -> Unit,
    onChangeMode: (SensorMode) -> Unit,
    onStartGame: () -> Unit,
    onExitGame: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    if (state.showHomeScreen) {
        HomeScreen(state, onChangeMode, onStartGame, onToggleLanguage)
    } else {
        MainGameView(state, onAim, onReset, onNextHole, onExitGame)
    }
}

@Composable
fun MainGameView(
    state: GameState,
    onAim: (Float) -> Unit,
    onReset: () -> Unit,
    onNextHole: () -> Unit,
    onExitGame: () -> Unit
) {
    val animatedX = remember { Animatable(state.ballX) }
    val animatedY = remember { Animatable(state.ballY) }

    LaunchedEffect(state.ballX, state.ballY, state.strokes) {
        if (state.strokes > 0 && state.waypoints.isNotEmpty()) {
            var totalDistance = 0f
            var prevX = animatedX.value
            var prevY = animatedY.value
            state.waypoints.forEach { wp ->
                totalDistance += sqrt((wp.first - prevX).pow(2) + (wp.second - prevY).pow(2))
                prevX = wp.first
                prevY = wp.second
            }

            prevX = animatedX.value
            prevY = animatedY.value
            val totalTime = 1200f

            state.waypoints.forEachIndexed { index, waypoint ->
                val isLast = index == state.waypoints.size - 1
                val segmentDistance = sqrt((waypoint.first - prevX).pow(2) + (waypoint.second - prevY).pow(2))
                val timePercentage = if (totalDistance > 0f) segmentDistance / totalDistance else 1f
                val duration = (totalTime * timePercentage).toInt().coerceAtLeast(10)

                val currentEasing = if (isLast) LinearOutSlowInEasing else LinearEasing

                val jobX = launch {
                    animatedX.animateTo(
                        targetValue = waypoint.first,
                        animationSpec = tween(durationMillis = duration, easing = currentEasing)
                    )
                }
                val jobY = launch {
                    animatedY.animateTo(
                        targetValue = waypoint.second,
                        animationSpec = tween(durationMillis = duration, easing = currentEasing)
                    )
                }

                jobX.join()
                jobY.join()

                prevX = waypoint.first
                prevY = waypoint.second
            }
        } else {
            animatedX.snapTo(state.ballX)
            animatedY.snapTo(state.ballY)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onExitGame,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (state.isEnglish) "Exit" else "Salir", color = Color.White)
            }

            Text(
                text = if (state.isEnglish) "Hole ${state.holeNumber}" else "Hoyo ${state.holeNumber}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (state.isEnglish) "Restart" else "Reiniciar", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Text(" Par: ${state.par} ", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Medium)
            }
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD54F))) {
                Text(
                    text = if (state.isEnglish) " Strokes: ${state.strokes} " else " Golpes: ${state.strokes} ",
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (state.isEnglish) "⚡ REAL-TIME SWING POWER" else "⚡ FUERZA DE SWING EN TIEMPO REAL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "${(state.currentSensorAcceleration * 100).toInt()}%",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = if (state.currentSensorAcceleration > 0.7f) Color.Red else Color(0xFF1B5E20)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color(0xFFE0E0E0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = state.currentSensorAcceleration.coerceIn(0f, 1f))
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF4CAF50), Color(0xFFFFC107), Color(0xFFF44336))
                                )
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .pointerInput(state.sensorMode) {
                    detectDragGestures { change, _ ->
                        if (state.sensorMode == SensorMode.TOUCH_AIM_SENSOR_FORCE && !state.holeCompleted) {
                            val touchPos = change.position
                            val ballPos = Offset(
                                x = animatedX.value * size.width,
                                y = animatedY.value * size.height
                            )
                            val deltaX = touchPos.x - ballPos.x
                            val deltaY = touchPos.y - ballPos.y
                            val angleRad = atan2(deltaY.toDouble(), deltaX.toDouble())
                            onAim(Math.toDegrees(angleRad).toFloat())
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val ballPosition = Offset(
                    x = animatedX.value * size.width,
                    y = animatedY.value * size.height
                )

                val holePosition = Offset(
                    x = state.holeX * size.width,
                    y = state.holeY * size.height
                )

                // Fondo y mapa base
                drawRect(color = GolfCourseGreen)
                drawRect(color = Color(0xFF1B5E20), style = Stroke(width = 10f))

                // Dibujar Obstáculos desde la configuración centralizada
                val currentObstacles = getObstaclesForLevel(state.holeNumber)
                currentObstacles.forEach { obs ->
                    // Base del muro
                    drawRect(
                        color = Color(0xFF5D4037),
                        topLeft = Offset(obs.x * size.width, obs.y * size.height),
                        size = Size(obs.width * size.width, obs.height * size.height)
                    )
                    // Borde decorativo
                    drawRect(
                        color = Color(0xFF3E2723),
                        topLeft = Offset(obs.x * size.width, obs.y * size.height),
                        size = Size(obs.width * size.width, obs.height * size.height),
                        style = Stroke(width = 4f)
                    )
                }

                // Hoyo
                drawCircle(
                    color = GolfHoleBlack,
                    radius = 35f,
                    center = holePosition
                )

                // Guía puntero
                if (!state.holeCompleted) {
                    val pointerLength = 180f
                    val angleRad = Math.toRadians(state.aimAngleDegrees.toDouble())
                    val endX = ballPosition.x + pointerLength * cos(angleRad).toFloat()
                    val endY = ballPosition.y + pointerLength * sin(angleRad).toFloat()

                    drawLine(
                        color = Color.Red,
                        start = ballPosition,
                        end = Offset(endX, endY),
                        strokeWidth = 6f,
                        cap = StrokeCap.Round
                    )
                }

                // Pelota
                val finalBallPos = if (state.holeCompleted) holePosition else ballPosition
                drawCircle(
                    color = GolfBallWhite,
                    radius = 18f,
                    center = finalBallPos
                )
            }
        }

        if (state.holeCompleted) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (state.isEnglish) "HOLE COMPLETED!" else "¡HOYO COMPLETADO!",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onNextHole,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text(if (state.isEnglish) "Next Level" else "Siguiente Nivel", color = Color.White)
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (state.sensorMode == SensorMode.TOUCH_AIM_SENSOR_FORCE) {
                    if (state.isEnglish) "Aim on screen and shake to shoot" else "Apunta en pantalla y agita para tirar"
                } else {
                    if (state.isEnglish) "Tilt mobile to aim and swing to shoot" else "Inclina el móvil para apuntar y tira"
                },
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}