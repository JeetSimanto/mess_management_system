package com.messmanager.app.data.remote.model

import com.google.firebase.firestore.ServerTimestamp
import com.messmanager.app.domain.model.Meal
import java.util.Date

data class MealDocument(
    val id: String = "",
    val memberUid: String = "",
    val memberName: String = "",
    val date: String = "",
    val count: Double = 0.0,
    val month: Int = 1,
    val year: Int = 2026,
    @ServerTimestamp val updatedAt: Date? = null
) {
    fun toDomain(): Meal = Meal(
        id = id,
        memberUid = memberUid,
        memberName = memberName,
        date = date,
        count = count,
        month = month,
        year = year,
        updatedAt = updatedAt?.time ?: System.currentTimeMillis()
    )

    companion object {
        fun fromDomain(meal: Meal): MealDocument = MealDocument(
            id = meal.id,
            memberUid = meal.memberUid,
            memberName = meal.memberName,
            date = meal.date,
            count = meal.count,
            month = meal.month,
            year = meal.year
        )
    }
}
