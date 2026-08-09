package com.messmanager.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.messmanager.app.data.remote.model.BorrowDocument
import com.messmanager.app.domain.model.BorrowRequest
import com.messmanager.app.domain.model.BorrowStatus
import com.messmanager.app.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BorrowRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun observeBorrows(messId: String): Flow<List<BorrowRequest>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_MESSES)
            .document(messId)
            .collection(Constants.SUBCOLLECTION_BORROW)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val borrows = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(BorrowDocument::class.java)?.toDomain()
                }?.sortedByDescending { it.createdAt } ?: emptyList()

                trySend(borrows)
            }

        awaitClose { listener.remove() }
    }

    suspend fun requestBorrow(messId: String, borrow: BorrowRequest): Result<Unit> {
        return try {
            val docRef = firestore.collection(Constants.COLLECTION_MESSES)
                .document(messId)
                .collection(Constants.SUBCOLLECTION_BORROW)
                .document()

            val doc = BorrowDocument.fromDomain(
                borrow.copy(
                    id = docRef.id,
                    status = BorrowStatus.PENDING
                )
            )
            docRef.set(doc).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resolveBorrow(
        messId: String,
        borrowId: String,
        accept: Boolean,
        dueDate: String = ""
    ): Result<Unit> {
        return try {
            val newStatus = if (accept) BorrowStatus.ACCEPTED else BorrowStatus.REJECTED
            val updates = mutableMapOf<String, Any>(
                "status" to newStatus.name,
                "resolvedAt" to System.currentTimeMillis()
            )
            if (accept && dueDate.isNotEmpty()) {
                updates["dueDate"] = dueDate
            }

            firestore.collection(Constants.COLLECTION_MESSES)
                .document(messId)
                .collection(Constants.SUBCOLLECTION_BORROW)
                .document(borrowId)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markReturned(messId: String, borrowId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_MESSES)
                .document(messId)
                .collection(Constants.SUBCOLLECTION_BORROW)
                .document(borrowId)
                .update(
                    mapOf(
                        "status" to BorrowStatus.RETURNED.name,
                        "returnedAt" to System.currentTimeMillis()
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
