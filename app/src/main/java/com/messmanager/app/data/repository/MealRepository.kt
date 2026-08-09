package com.messmanager.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.messmanager.app.data.remote.model.MealDocument
import com.messmanager.app.domain.model.Meal
import com.messmanager.app.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun observeMeals(messId: String, month: Int, year: Int): Flow<List<Meal>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_MESSES)
            .document(messId)
            .collection(Constants.SUBCOLLECTION_MEAL)
            .whereEqualTo("month", month)
            .whereEqualTo("year", year)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val meals = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(MealDocument::class.java)?.toDomain()
                } ?: emptyList()

                trySend(meals)
            }

        awaitClose { listener.remove() }
    }

    suspend fun setMeal(messId: String, meal: Meal): Result<Unit> {
        return try {
            // Document ID convention: memberUid_date (e.g. user123_2026-08-05)
            val docId = "${meal.memberUid}_${meal.date}"
            val docRef = firestore.collection(Constants.COLLECTION_MESSES)
                .document(messId)
                .collection(Constants.SUBCOLLECTION_MEAL)
                .document(docId)

            val doc = MealDocument.fromDomain(meal.copy(id = docId))
            docRef.set(doc).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
