package com.messmanager.app.domain.model

data class Mess(
    val id: String = "",
    val name: String = "",
    val inviteCode: String = "",
    val managerId: String = "",
    val month: Int = 1,
    val year: Int = 2026,
    val memberIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
