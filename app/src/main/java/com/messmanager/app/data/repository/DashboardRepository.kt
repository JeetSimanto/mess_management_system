package com.messmanager.app.data.repository

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.messmanager.app.data.remote.model.UserDocument
import com.messmanager.app.domain.calculator.SettlementCalculator
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.Mess
import com.messmanager.app.domain.model.MessRole
import com.messmanager.app.domain.model.MessSettlement
import com.messmanager.app.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val groceryRepository: GroceryRepository,
    private val utilityRepository: UtilityRepository,
    private val mealRepository: MealRepository,
    private val contributionRepository: ContributionRepository
) {
    suspend fun getMessMembers(mess: Mess): List<Member> {
        if (mess.memberIds.isEmpty()) return emptyList()

        return try {
            val querySnapshot = firestore.collection(Constants.COLLECTION_USERS)
                .whereIn(FieldPath.documentId(), mess.memberIds)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { doc ->
                val userDoc = doc.toObject(UserDocument::class.java) ?: return@mapNotNull null
                val role = if (userDoc.uid == mess.managerId) MessRole.MANAGER else MessRole.MEMBER
                Member(
                    uid = userDoc.uid,
                    displayName = userDoc.displayName.ifEmpty { "Member" },
                    email = userDoc.email,
                    photoUrl = userDoc.photoUrl,
                    role = role
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun observeMessMembers(mess: Mess): Flow<List<Member>> = callbackFlow {
        if (mess.memberIds.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(Constants.COLLECTION_USERS)
            .whereIn(FieldPath.documentId(), mess.memberIds)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val members = snapshot?.documents?.mapNotNull { doc ->
                    val userDoc = doc.toObject(UserDocument::class.java) ?: return@mapNotNull null
                    val role = if (userDoc.uid == mess.managerId) MessRole.MANAGER else MessRole.MEMBER
                    Member(
                        uid = userDoc.uid,
                        displayName = userDoc.displayName.ifEmpty { "Member" },
                        email = userDoc.email,
                        photoUrl = userDoc.photoUrl,
                        role = role
                    )
                } ?: emptyList()

                trySend(members)
            }

        awaitClose { listener.remove() }
    }

    fun observeMessSettlement(mess: Mess): Flow<MessSettlement> {
        val membersFlow = observeMessMembers(mess)
        val groceriesFlow = groceryRepository.observeGroceries(mess.id, mess.month, mess.year)
        val utilitiesFlow = utilityRepository.observeUtilities(mess.id, mess.month, mess.year)
        val mealsFlow = mealRepository.observeMeals(mess.id, mess.month, mess.year)
        val contributionsFlow = contributionRepository.observeContributions(mess.id, mess.month, mess.year)

        return combine(
            membersFlow,
            groceriesFlow,
            utilitiesFlow,
            mealsFlow,
            contributionsFlow
        ) { members, groceries, utilities, meals, contributions ->
            SettlementCalculator.calculateSettlement(
                messId = mess.id,
                month = mess.month,
                year = mess.year,
                members = members,
                groceries = groceries,
                utilities = utilities,
                meals = meals,
                contributions = contributions,
                fixedMealCount = mess.fixedMealCount
            )
        }
    }
}
