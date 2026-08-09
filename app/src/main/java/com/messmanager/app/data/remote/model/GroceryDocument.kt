package com.messmanager.app.data.remote.model

import com.google.firebase.firestore.ServerTimestamp
import com.messmanager.app.domain.model.Grocery
import java.util.Date

data class GroceryDocument(
    val id: String = "",
    val itemName: String = "",
    val quantity: Double = 0.0,
    val unit: String = "kg",
    val costPaisa: Long = 0,
    val buyerUid: String = "",
    val buyerName: String = "",
    val date: String = "",
    val note: String = "",
    val month: Int = 1,
    val year: Int = 2026,
    @ServerTimestamp val createdAt: Date? = null
) {
    fun toDomain(): Grocery = Grocery(
        id = id,
        itemName = itemName,
        quantity = quantity,
        unit = unit,
        costPaisa = costPaisa,
        buyerUid = buyerUid,
        buyerName = buyerName,
        date = date,
        note = note,
        month = month,
        year = year,
        createdAt = createdAt?.time ?: System.currentTimeMillis()
    )

    companion object {
        fun fromDomain(grocery: Grocery): GroceryDocument = GroceryDocument(
            id = grocery.id,
            itemName = grocery.itemName,
            quantity = grocery.quantity,
            unit = grocery.unit,
            costPaisa = grocery.costPaisa,
            buyerUid = grocery.buyerUid,
            buyerName = grocery.buyerName,
            date = grocery.date,
            note = grocery.note,
            month = grocery.month,
            year = grocery.year
        )
    }
}
