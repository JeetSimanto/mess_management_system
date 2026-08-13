package com.messmanager.app.domain.model

enum class SettlementStatus {
    GET_BACK, // Positive balance — mess owes member money
    PAY_EXTRA, // Negative balance — member owes mess money
    SETTLED   // Zero balance
}

data class MemberSettlement(
    val memberUid: String = "",
    val memberName: String = "",
    val totalMeals: Double = 0.0,
    val groceryCostPaisa: Long = 0,
    val utilitySharePaisa: Long = 0,
    val totalCostPaisa: Long = 0,
    val totalContributionPaisa: Long = 0,
    val balancePaisa: Long = 0,
    val status: SettlementStatus = SettlementStatus.SETTLED
) {
    val totalCostBdt: Double get() = totalCostPaisa / 100.0
    val totalContributionBdt: Double get() = totalContributionPaisa / 100.0
    val balanceBdt: Double get() = balancePaisa / 100.0
    val groceryCostBdt: Double get() = groceryCostPaisa / 100.0
    val utilityShareBdt: Double get() = utilitySharePaisa / 100.0
}

data class MessSettlement(
    val messId: String = "",
    val month: Int = 1,
    val year: Int = 2026,
    val totalGroceryPaisa: Long = 0,
    val totalUtilityPaisa: Long = 0,
    val totalExpensePaisa: Long = 0,
    val totalContributionPaisa: Long = 0,
    val moneyRemainsPaisa: Long = 0, // Fund balance in manager's hands
    val totalMeals: Double = 0.0,
    val mealRatePaisaPerMeal: Double = 0.0,
    val memberSettlements: List<MemberSettlement> = emptyList()
) {
    val totalGroceryBdt: Double get() = totalGroceryPaisa / 100.0
    val totalUtilityBdt: Double get() = totalUtilityPaisa / 100.0
    val totalExpenseBdt: Double get() = totalExpensePaisa / 100.0
    val totalContributionBdt: Double get() = totalContributionPaisa / 100.0
    val moneyRemainsBdt: Double get() = moneyRemainsPaisa / 100.0
    val mealRateBdtPerMeal: Double get() = mealRatePaisaPerMeal / 100.0
}
