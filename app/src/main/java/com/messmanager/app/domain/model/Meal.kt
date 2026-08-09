package com.messmanager.app.domain.model

data class Meal(
    val id: String = "",
    val memberUid: String = "",
    val memberName: String = "",
    val date: String = "", // YYYY-MM-DD
    val count: Double = 0.0, // Supports 0, 0.5, 1, 1.5, 2, 2.5, 3
    val month: Int = 1,
    val year: Int = 2026,
    val updatedAt: Long = System.currentTimeMillis()
)
