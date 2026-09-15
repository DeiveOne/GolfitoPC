package com.example.golfitopc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.sp
import com.example.golfitopc.game.GameState
import com.example.golfitopc.game.SensorMode
import com.example.golfitopc.ui.Strings

@Composable
fun HomeScreen(
    state: GameState,
    onChangeMode: (SensorMode) -> Unit,
    onStartGame: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1B5E20), Color(0xFF4CAF50))))
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Fila superior para el cambio de idioma
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onToggleLanguage,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (state.isEnglish) "🇺🇸 English ➔ 🇪🇸" else "🇪🇸 Español ➔ 🇺🇸",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("⛳", fontSize = 55.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = Strings.get("app_name", state.isEnglish),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        // SCOREBOARD/SCORECARD HISTÓRICO VISIBLE EN PANTALLA PRINCIPAL DE HOME
        if (state.holeScores.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = Strings.get("scoreboard_title", state.isEnglish),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = Color(0xFF1B5E20)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // TABLA DE PUNTUACIONES TEMÁTICA
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF1B5E20).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    ) {
                        // Fila de Encabezados
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFC8E6C9))
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Strings.get("table_hole", state.isEnglish),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = Strings.get("table_strokes", state.isEnglish),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B5E20)
                            )
                        }

                        // Filas de Datos
                        state.holeScores.entries.sortedBy { it.key }.forEachIndexed { index, entry ->
                            val rowBackground = if (index % 2 == 0) Color.White else Color(0xFFF1F8E9)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(rowBackground)
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${entry.key}",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF37474F)
                                )
                                Text(
                                    text = "${entry.value}",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF37474F)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Strings.get("instructions_title", state.isEnglish),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1B5E20)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = Strings.get("instructions_desc", state.isEnglish),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = Strings.get("mode_touch_title", state.isEnglish),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF283593)
                )
                Text(
                    text = Strings.get("mode_touch_desc", state.isEnglish),
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = Strings.get("mode_sensor_title", state.isEnglish),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF00695C)
                )
                Text(
                    text = Strings.get("mode_sensor_desc", state.isEnglish),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = Strings.get("select_mode", state.isEnglish),
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { onChangeMode(SensorMode.TOUCH_AIM_SENSOR_FORCE) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.sensorMode == SensorMode.TOUCH_AIM_SENSOR_FORCE) Color(0xFF283593) else Color.White.copy(alpha = 0.3f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(Strings.get("btn_touch_force", state.isEnglish), color = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onChangeMode(SensorMode.FULL_SENSOR) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.sensorMode == SensorMode.FULL_SENSOR) Color(0xFF00695C) else Color.White.copy(alpha = 0.3f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(Strings.get("btn_full_sensor", state.isEnglish), color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onStartGame,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = Strings.get("btn_play", state.isEnglish),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color(0xFF424242)
            )
        }
    }
}
