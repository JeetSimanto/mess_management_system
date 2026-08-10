package com.messmanager.app.domain.calculator

import com.messmanager.app.domain.model.Contribution
import com.messmanager.app.domain.model.Grocery
import com.messmanager.app.domain.model.Meal
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.MemberSettlement
import com.messmanager.app.domain.model.MessSettlement
import com.messmanager.app.domain.model.SettlementStatus
import com.messmanager.app.domain.model.Utility
import kotlin.math.roundToLong

object SettlementCalculator {

    fun calculateSettlement(
        messId: String,
        month: Int,
        year: Int,
        members: List<Member>,
        groceries: List<Grocery>,
        utilities: List<Utility>,
        meals: List<Meal>,
        contributions: List<Contribution>,
        fixedMealCount: Double = 0.0
    ): MessSettlement {
        val totalGroceryPaisa = groceries.sumOf { it.costPaisa }
        val totalUtilityPaisa = utilities.sumOf { it.costPaisa }
        val totalExpensePaisa = totalGroceryPaisa + totalUtilityPaisa
        val totalContributionPaisa = contributions.sumOf { it.amountPaisa }

        val rawTotalMeals = meals.sumOf { it.count }

        val memberMealMap = members.associate { member ->
            val actualMeals = meals.filter { it.memberUid == member.uid }.sumOf { it.count }
            val effectiveMeals = if (fixedMealCount > 0.0 && actualMeals < fixedMealCount) fixedMealCount else actualMeals
            member.uid to Pair(actualMeals, effectiveMeals)
        }

        val totalEffectiveMeals = memberMealMap.values.sumOf { it.second }
        val totalMealsForRate = if (totalEffectiveMeals > 0.0) totalEffectiveMeals else rawTotalMeals

        val mealRatePaisaPerMeal = if (totalMealsForRate > 0.0) {
            totalGroceryPaisa.toDouble() / totalMealsForRate
        } else {
            0.0
        }

        val memberCount = members.size
        val utilitySharePerMemberPaisa = if (memberCount > 0) {
            totalUtilityPaisa / memberCount
        } else {
            0L
        }

        val memberSettlements = members.map { member ->
            val (actualMeals, effectiveMeals) = memberMealMap[member.uid] ?: Pair(0.0, 0.0)
            val isFixedApplied = fixedMealCount > 0.0 && actualMeals < fixedMealCount
            val groceryCostPaisa = (effectiveMeals * mealRatePaisaPerMeal).roundToLong()
            val utilitySharePaisa = utilitySharePerMemberPaisa
            val totalCostPaisa = groceryCostPaisa + utilitySharePaisa

            val memberContributionPaisa = contributions
                .filter { it.memberUid == member.uid }
                .sumOf { it.amountPaisa }

            val balancePaisa = memberContributionPaisa - totalCostPaisa

            val status = when {
                balancePaisa > 0 -> SettlementStatus.GET_BACK
                balancePaisa < 0 -> SettlementStatus.PAY_EXTRA
                else -> SettlementStatus.SETTLED
            }

            MemberSettlement(
                memberUid = member.uid,
                memberName = member.displayName,
                totalMeals = effectiveMeals,
                rawMeals = actualMeals,
                isFixedMealApplied = isFixedApplied,
                groceryCostPaisa = groceryCostPaisa,
                utilitySharePaisa = utilitySharePaisa,
                totalCostPaisa = totalCostPaisa,
                totalContributionPaisa = memberContributionPaisa,
                balancePaisa = balancePaisa,
                status = status
            )
        }

        val moneyRemainsPaisa = totalContributionPaisa - totalExpensePaisa

        return MessSettlement(
            messId = messId,
            month = month,
            year = year,
            totalGroceryPaisa = totalGroceryPaisa,
            totalUtilityPaisa = totalUtilityPaisa,
            totalExpensePaisa = totalExpensePaisa,
            totalContributionPaisa = totalContributionPaisa,
            moneyRemainsPaisa = moneyRemainsPaisa,
            totalMeals = totalEffectiveMeals,
            mealRatePaisaPerMeal = mealRatePaisaPerMeal,
            memberSettlements = memberSettlements
        )
    }
}
