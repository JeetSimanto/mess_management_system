package com.messmanager.app.domain.model

data class MealRate(
    val totalGroceryPaisa: Long = 0,
    val totalMeals: Double = 0.0,
    val ratePaisaPerMeal: Double = 0.0
) {
    val totalGroceryBdt: Double
        get() = totalGroceryPaisa / 100.0

    val rateBdtPerMeal: Double
        get() = ratePaisaPerMeal / 100.0
}
