package com.messmanager.app.domain.model

data class Utility(
    val id: String = "",
    val title: String = "",
    val category: String = "Other", // Rent, Electricity, Gas, Water, Waste, Transport, WiFi, Other
    val costPaisa: Long = 0,
    val date: String = "",
    val month: Int = 1,
    val year: Int = 2026,
    val createdAt: Long = System.currentTimeMillis()
) {
    val costBdt: Double
        get() = costPaisa / 100.0
}
