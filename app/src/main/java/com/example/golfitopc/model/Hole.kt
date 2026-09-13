package com.example.golfitopc.model

data class Hole(
    val number: Int,
    val par: Int,
    val startX: Float,
    val startY: Float,
    val targetX: Float,
    val targetY: Float
)