package com.messmanager.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.messmanager.app.data.remote.model.UtilityDocument
import com.messmanager.app.domain.model.Utility
import com.messmanager.app.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UtilityRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun observeUtilities(messId: String, month: Int, year: Int): Flow<List<Utility>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_MESSES)
            .document(messId)
            .collection(Constants.SUBCOLLECTION_UTILITY)
            .whereEqualTo("month", month)
            .whereEqualTo("year", year)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val utilities = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(UtilityDocument::class.java)?.toDomain()
                }?.sortedByDescending { it.date } ?: emptyList()

                trySend(utilities)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addUtility(messId: String, utility: Utility): Result<Unit> {
        return try {
            val docRef = firestore.collection(Constants.COLLECTION_MESSES)
                .document(messId)
                .collection(Constants.SUBCOLLECTION_UTILITY)
                .document()

            val doc = UtilityDocument.fromDomain(utility.copy(id = docRef.id))
            docRef.set(doc).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUtility(messId: String, utility: Utility): Result<Unit> {
        return try {
            val docRef = firestore.collection(Constants.COLLECTION_MESSES)
                .document(messId)
                .collection(Constants.SUBCOLLECTION_UTILITY)
                .document(utility.id)

            val doc = UtilityDocument.fromDomain(utility)
            docRef.set(doc).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUtility(messId: String, utilityId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_MESSES)
                .document(messId)
                .collection(Constants.SUBCOLLECTION_UTILITY)
                .document(utilityId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
