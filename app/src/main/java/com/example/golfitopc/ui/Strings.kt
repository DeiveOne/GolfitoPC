package com.example.golfitopc.ui

object Strings {
    fun get(key: String, isEnglish: Boolean): String {
        val translations = if (isEnglish) english else spanish
        return translations[key] ?: key
    }

    private val english = mapOf(
        "app_name" to "Golfito PC",
        "instructions_title" to "📖 Instructions:",
        "instructions_desc" to "Select your preferred shot mode and get ready for the swing.",
        "mode_touch_title" to "TOUCH AIM & SENSOR FORCE:",
        "mode_touch_desc" to "Drag your finger on the screen to aim, then shake the device to strike.",
        "mode_sensor_title" to "FULL SENSOR MODE:",
        "mode_sensor_desc" to "Tilt the device to aim and perform a physical swinging motion to strike.",
        "select_mode" to "Select Shot Mode:",
        "btn_touch_force" to "Touch + Force",
        "btn_full_sensor" to "Full Sensor",
        "btn_play" to "PLAY NOW!",
        "scoreboard_title" to "🏆 YOUR CURRENT SCOREBOARD 🏆",
        "table_hole" to "Hole",
        "table_strokes" to "Strokes",
        "btn_exit" to "Exit",
        "btn_restart" to "Restart",
        "label_par" to "Par:",
        "label_strokes" to "Strokes:",
        "label_power" to "⚡ REAL-TIME SWING POWER",
        "hole_completed" to "HOLE COMPLETED!",
        "btn_next_level" to "Next Level",
        "hint_touch" to "Aim on screen and shake to shoot",
        "hint_sensor" to "Tilt mobile to aim and swing to shoot"
    )

    private val spanish = mapOf(
        "app_name" to "Golfito PC",
        "instructions_title" to "📖 Instrucciones:",
        "instructions_desc" to "Selecciona tu modo de golpeo preferido y prepárate para el swing.",
        "mode_touch_title" to "TOQUE PARA APUNTAR Y FUERZA SENSOR:",
        "mode_touch_desc" to "Arrastra tu dedo en la pantalla para apuntar, luego agita el dispositivo para golpear.",
        "mode_sensor_title" to "MODO SENSOR COMPLETO:",
        "mode_sensor_desc" to "Inclina el dispositivo para apuntar y realiza un movimiento físico de swing para golpear.",
        "select_mode" to "Selecciona Modo de Golpeo:",
        "btn_touch_force" to "Toque + Fuerza",
        "btn_full_sensor" to "Sensor Completo",
        "btn_play" to "¡JUGAR AHORA!",
        "scoreboard_title" to "🏆 TU TABLA DE PUNTUACIONES 🏆",
        "table_hole" to "Hoyo",
        "table_strokes" to "Golpes",
        "btn_exit" to "Salir",
        "btn_restart" to "Reiniciar",
        "label_par" to "Par:",
        "label_strokes" to "Golpes:",
        "label_power" to "⚡ FUERZA DE SWING EN TIEMPO REAL",
        "hole_completed" to "¡HOYO COMPLETADO!",
        "btn_next_level" to "Siguiente Nivel",
        "hint_touch" to "Apunta en pantalla y agita para tirar",
        "hint_sensor" to "Inclina el móvil para apuntar y tira"
    )
}
