package com.messmanager.app.data.remote.model

import com.google.firebase.firestore.ServerTimestamp
import com.messmanager.app.domain.model.Utility
import java.util.Date

data class UtilityDocument(
    val id: String = "",
    val title: String = "",
    val category: String = "Other",
    val costPaisa: Long = 0,
    val date: String = "",
    val month: Int = 1,
    val year: Int = 2026,
    @ServerTimestamp val createdAt: Date? = null
) {
    fun toDomain(): Utility = Utility(
        id = id,
        title = title,
        category = category,
        costPaisa = costPaisa,
        date = date,
        month = month,
        year = year,
        createdAt = createdAt?.time ?: System.currentTimeMillis()
    )

    companion object {
        fun fromDomain(utility: Utility): UtilityDocument = UtilityDocument(
            id = utility.id,
            title = utility.title,
            category = utility.category,
            costPaisa = utility.costPaisa,
            date = utility.date,
            month = utility.month,
            year = utility.year
        )
    }
}
