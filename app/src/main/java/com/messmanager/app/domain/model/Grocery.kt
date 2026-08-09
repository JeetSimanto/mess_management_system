package com.messmanager.app.domain.model

data class Grocery(
    val id: String = "",
    val itemName: String = "",
    val quantity: Double = 0.0,
    val unit: String = "kg",
    val costPaisa: Long = 0,
    val buyerUid: String = "",
    val buyerName: String = "",
    val date: String = "", // ISO YYYY-MM-DD
    val note: String = "",
    val month: Int = 1,
    val year: Int = 2026,
    val createdAt: Long = System.currentTimeMillis()
) {
    val costBdt: Double
        get() = costPaisa / 100.0
}
