package com.messmanager.app.domain.model

data class Contribution(
    val id: String = "",
    val memberUid: String = "",
    val memberName: String = "",
    val amountPaisa: Long = 0,
    val date: String = "",
    val purpose: String = "Deposit",
    val month: Int = 1,
    val year: Int = 2026,
    val createdAt: Long = System.currentTimeMillis()
) {
    val amountBdt: Double
        get() = amountPaisa / 100.0
}
