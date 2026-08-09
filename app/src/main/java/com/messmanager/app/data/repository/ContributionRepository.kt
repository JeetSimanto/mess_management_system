package com.messmanager.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.messmanager.app.data.remote.model.ContributionDocument
import com.messmanager.app.domain.model.Contribution
import com.messmanager.app.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContributionRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun observeContributions(messId: String, month: Int, year: Int): Flow<List<Contribution>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_MESSES)
            .document(messId)
            .collection(Constants.SUBCOLLECTION_CONTRIBUTION)
            .whereEqualTo("month", month)
            .whereEqualTo("year", year)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val contributions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ContributionDocument::class.java)?.toDomain()
                }?.sortedByDescending { it.date } ?: emptyList()

                trySend(contributions)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addContribution(messId: String, contribution: Contribution): Result<Unit> {
        return try {
            val docRef = firestore.collection(Constants.COLLECTION_MESSES)
                .document(messId)
                .collection(Constants.SUBCOLLECTION_CONTRIBUTION)
                .document()

            val doc = ContributionDocument.fromDomain(contribution.copy(id = docRef.id))
            docRef.set(doc).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateContribution(messId: String, contribution: Contribution): Result<Unit> {
        return try {
            val docRef = firestore.collection(Constants.COLLECTION_MESSES)
                .document(messId)
                .collection(Constants.SUBCOLLECTION_CONTRIBUTION)
                .document(contribution.id)

            val doc = ContributionDocument.fromDomain(contribution)
            docRef.set(doc).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteContribution(messId: String, contributionId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_MESSES)
                .document(messId)
                .collection(Constants.SUBCOLLECTION_CONTRIBUTION)
                .document(contributionId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
