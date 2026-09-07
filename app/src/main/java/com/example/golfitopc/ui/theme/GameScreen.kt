package com.example.golfitopc.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.golfitopc.game.GameState
import com.example.golfitopc.ui.theme.GolfBallWhite
import com.example.golfitopc.ui.theme.GolfCourseGreen
import com.example.golfitopc.ui.theme.GolfHoleBlack
@Composable
fun GameScreen(
    state: GameState,
    onShot: () -> Unit,
    onReset: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "MINI GOLF"
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Text(
                text = "Hoyo: ${state.holeNumber}"
            )

            Text(
                text = "Par: ${state.par}"
            )

            Text(
                text = "Golpes: ${state.strokes}"
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            val ballPosition = Offset(
                x = state.ballX * size.width,
                y = state.ballY * size.height
            )

            val holePosition = Offset(
                x = state.holeX * size.width,
                y = state.holeY * size.height
            )

            // Campo
            drawRect(
                color = GolfCourseGreen
            )

            // Hoyo
            drawCircle(
                color = GolfHoleBlack,
                radius = 25f,
                center = holePosition
            )

            // Pelota
            drawCircle(
                color = GolfBallWhite,
                radius = 18f,
                center = ballPosition
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        if (state.holeCompleted) {

            Text(
                text = "¡HOYO COMPLETADO!"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        Button(
            onClick = onShot,
            enabled = !state.holeCompleted
        ) {
            Text(
                text = "Golpe de prueba"
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = onReset
        ) {
            Text(
                text = "Reiniciar hoyo"
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Mueve el celular para realizar un golpe"
        )
    }
}