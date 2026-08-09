package com.messmanager.app.domain.model

enum class BorrowStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    RETURNED
}

data class BorrowRequest(
    val id: String = "",
    val requesterUid: String = "",
    val requesterName: String = "",
    val itemName: String = "",
    val quantity: String = "",
    val status: BorrowStatus = BorrowStatus.PENDING,
    val date: String = "", // Date borrowed
    val dueDate: String = "", // Set by manager on approval
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null,
    val returnedAt: Long? = null
)
