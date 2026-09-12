package com.example.golfitopc.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.golfitopc.game.GameState
import com.example.golfitopc.ui.theme.GolfBallWhite
import com.example.golfitopc.ui.theme.GolfCourseGreen
import com.example.golfitopc.ui.theme.GolfHoleBlack
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun GameScreen(
    state: GameState,
    onAim: (Float) -> Unit,
    onShot: () -> Unit,
    onReset: () -> Unit
) {
    val animatedX = remember { Animatable(state.ballX) }
    val animatedY = remember { Animatable(state.ballY) }

    LaunchedEffect(state.strokes) {
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
            val totalTime = 1500f

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

                jobX.join() // Bloquea hasta chocar con la pared
                jobY.join()

                prevX = waypoint.first
                prevY = waypoint.second
            }
        } else if (state.strokes == 0) {
            animatedX.snapTo(state.ballX)
            animatedY.snapTo(state.ballY)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "MINI GOLF")

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = "Hoyo: ${state.holeNumber}")
            Text(text = "Par: ${state.par}")
            Text(text = "Golpes: ${state.strokes}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val touchPos = change.position
                        val ballPos = Offset(
                            x = animatedX.value * size.width,
                            y = animatedY.value * size.height
                        )
                        val deltaX = touchPos.x - ballPos.x
                        val deltaY = touchPos.y - ballPos.y
                        val angleRad = kotlin.math.atan2(deltaY.toDouble(), deltaX.toDouble())
                        onAim(Math.toDegrees(angleRad).toFloat())
                    }
                }
        ) {
            val ballPosition = Offset(
                x = animatedX.value * size.width,
                y = animatedY.value * size.height
            )

            val holePosition = Offset(
                x = state.holeX * size.width,
                y = state.holeY * size.height
            )

            // Campo
            drawRect(color = GolfCourseGreen)

            // Hoyo
            drawCircle(
                color = GolfHoleBlack,
                radius = 25f,
                center = holePosition
            )

            // Línea del Puntero (solo se muestra si la pelota no se está moviendo hacia el hoyo y no ha completado el nivel)
            if (!state.holeCompleted) {
                val pointerLength = 150f
                val angleRad = Math.toRadians(state.aimAngleDegrees.toDouble())
                val endX = ballPosition.x + pointerLength * kotlin.math.cos(angleRad).toFloat()
                val endY = ballPosition.y + pointerLength * kotlin.math.sin(angleRad).toFloat()

                drawLine(
                    color = Color.Red,
                    start = ballPosition,
                    end = Offset(endX, endY),
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
            }

            // Pelota
            drawCircle(
                color = GolfBallWhite,
                radius = 18f,
                center = ballPosition
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.holeCompleted) {
            Text(text = "¡HOYO COMPLETADO!")
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = onShot, // Botón para pruebas manuales si agitar el celular es incómodo
            enabled = !state.holeCompleted
        ) {
            Text(text = "Golpe de prueba")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onReset) {
            Text(text = "Reiniciar hoyo")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Apunta en pantalla y mueve el celular para golpear")
    }
}