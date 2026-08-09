package com.messmanager.app.data.remote.model

import com.google.firebase.firestore.ServerTimestamp
import com.messmanager.app.domain.model.BorrowRequest
import com.messmanager.app.domain.model.BorrowStatus
import java.util.Date

data class BorrowDocument(
    val id: String = "",
    val requesterUid: String = "",
    val requesterName: String = "",
    val itemName: String = "",
    val quantity: String = "",
    val status: String = BorrowStatus.PENDING.name,
    val date: String = "",
    val dueDate: String = "",
    @ServerTimestamp val createdAt: Date? = null,
    val resolvedAt: Long? = null,
    val returnedAt: Long? = null
) {
    fun toDomain(): BorrowRequest = BorrowRequest(
        id = id,
        requesterUid = requesterUid,
        requesterName = requesterName,
        itemName = itemName,
        quantity = quantity,
        status = try { BorrowStatus.valueOf(status) } catch (e: Exception) { BorrowStatus.PENDING },
        date = date,
        dueDate = dueDate,
        createdAt = createdAt?.time ?: System.currentTimeMillis(),
        resolvedAt = resolvedAt,
        returnedAt = returnedAt
    )

    companion object {
        fun fromDomain(borrow: BorrowRequest): BorrowDocument = BorrowDocument(
            id = borrow.id,
            requesterUid = borrow.requesterUid,
            requesterName = borrow.requesterName,
            itemName = borrow.itemName,
            quantity = borrow.quantity,
            status = borrow.status.name,
            date = borrow.date,
            dueDate = borrow.dueDate,
            resolvedAt = borrow.resolvedAt,
            returnedAt = borrow.returnedAt
        )
    }
}
