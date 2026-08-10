package com.messmanager.app.data.remote.model

import com.google.firebase.firestore.ServerTimestamp
import com.messmanager.app.domain.model.Mess
import java.util.Date

data class MessDocument(
    val id: String = "",
    val name: String = "",
    val inviteCode: String = "",
    val managerId: String = "",
    val month: Int = 1,
    val year: Int = 2026,
    val memberIds: List<String> = emptyList(),
    val fixedMealCount: Double = 0.0,
    val isSettled: Boolean = false,
    @ServerTimestamp val createdAt: Date? = null
) {
    fun toDomain(): Mess = Mess(
        id = id,
        name = name,
        inviteCode = inviteCode,
        managerId = managerId,
        month = month,
        year = year,
        memberIds = memberIds,
        fixedMealCount = fixedMealCount,
        isSettled = isSettled,
        createdAt = createdAt?.time ?: System.currentTimeMillis()
    )

    companion object {
        fun fromDomain(mess: Mess): MessDocument = MessDocument(
            id = mess.id,
            name = mess.name,
            inviteCode = mess.inviteCode,
            managerId = mess.managerId,
            month = mess.month,
            year = mess.year,
            memberIds = mess.memberIds,
            fixedMealCount = mess.fixedMealCount,
            isSettled = mess.isSettled
        )
    }
}
