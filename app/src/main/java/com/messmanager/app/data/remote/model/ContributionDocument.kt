package com.messmanager.app.data.remote.model

import com.google.firebase.firestore.ServerTimestamp
import com.messmanager.app.domain.model.Contribution
import java.util.Date

data class ContributionDocument(
    val id: String = "",
    val memberUid: String = "",
    val memberName: String = "",
    val amountPaisa: Long = 0,
    val date: String = "",
    val purpose: String = "Deposit",
    val month: Int = 1,
    val year: Int = 2026,
    @ServerTimestamp val createdAt: Date? = null
) {
    fun toDomain(): Contribution = Contribution(
        id = id,
        memberUid = memberUid,
        memberName = memberName,
        amountPaisa = amountPaisa,
        date = date,
        purpose = purpose,
        month = month,
        year = year,
        createdAt = createdAt?.time ?: System.currentTimeMillis()
    )

    companion object {
        fun fromDomain(contribution: Contribution): ContributionDocument = ContributionDocument(
            id = contribution.id,
            memberUid = contribution.memberUid,
            memberName = contribution.memberName,
            amountPaisa = contribution.amountPaisa,
            date = contribution.date,
            purpose = contribution.purpose,
            month = contribution.month,
            year = contribution.year
        )
    }
}
