package com.example.golfitopc.ui

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
            text = "Golfito PC",
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
                        text = if (state.isEnglish) "🏆 YOUR CURRENT SCOREBOARD 🏆" else "🏆 TU TABLA DE PUNTUACIONES 🏆",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = Color(0xFF1B5E20)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // NUEVA TABLA DE PUNTUACIONES MÁS ESTÉTICA CON PALETA VERDE DE GOLF
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF1B5E20).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    ) {
                        // Fila de Encabezados (Verde Temático)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFC8E6C9))
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (state.isEnglish) "Hole" else "Hoyo",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = if (state.isEnglish) "Strokes" else "Golpes",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B5E20)
                            )
                        }

                        // Filas de Datos (Gama de blancos y verdes sutiles)
                        state.holeScores.entries.sortedBy { it.key }.forEachIndexed { index, entry ->
                            // Alternar color para efecto "cebra" usando la gama verde
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
                    text = if (state.isEnglish) "📖 Instructions:" else "📖 Instrucciones:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1B5E20)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (state.isEnglish) {
                        "Select your preferred shot mode and get ready for the swing."
                    } else {
                        "Selecciona tu modo de golpeo preferido y prepárate para el swing."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (state.isEnglish) "TOUCH AIM & SENSOR FORCE:" else "TOQUE PARA APUNTAR Y FUERZA SENSOR:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF283593)
                )
                Text(
                    text = if (state.isEnglish) {
                        "Drag your finger on the screen to aim, then shake the device to strike."
                    } else {
                        "Arrastra tu dedo en la pantalla para apuntar, luego agita el dispositivo para golpear."
                    },
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (state.isEnglish) "FULL SENSOR MODE:" else "MODO SENSOR COMPLETO:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF00695C)
                )
                Text(
                    text = if (state.isEnglish) {
                        "Tilt the device to aim and perform a physical swinging motion to strike."
                    } else {
                        "Inclina el dispositivo para apuntar y realiza un movimiento físico de swing para golpear."
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (state.isEnglish) "Select Shot Mode:" else "Selecciona Modo de Golpeo:",
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
                Text(if (state.isEnglish) "Touch + Force" else "Toque + Fuerza", color = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onChangeMode(SensorMode.FULL_SENSOR) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.sensorMode == SensorMode.FULL_SENSOR) Color(0xFF00695C) else Color.White.copy(alpha = 0.3f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(if (state.isEnglish) "Full Sensor" else "Sensor Completo", color = Color.White)
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
                text = if (state.isEnglish) "PLAY NOW!" else "¡JUGAR AHORA!",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color(0xFF424242)
            )
        }
    }
}