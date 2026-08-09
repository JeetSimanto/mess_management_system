package com.messmanager.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.messmanager.app.data.remote.model.GroceryDocument
import com.messmanager.app.domain.model.Grocery
import com.messmanager.app.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroceryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun observeGroceries(messId: String, month: Int, year: Int): Flow<List<Grocery>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_MESSES)
            .document(messId)
            .collection(Constants.SUBCOLLECTION_GROCERY)
            .whereEqualTo("month", month)
            .whereEqualTo("year", year)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val groceries = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(GroceryDocument::class.java)?.toDomain()
                }?.sortedByDescending { it.date } ?: emptyList()

                trySend(groceries)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addGrocery(messId: String, grocery: Grocery): Result<Unit> {
        return try {
            val docRef = firestore.collection(Constants.COLLECTION_MESSES)
                .document(messId)
                .collection(Constants.SUBCOLLECTION_GROCERY)
                .document()

            val doc = GroceryDocument.fromDomain(grocery.copy(id = docRef.id))
            docRef.set(doc).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateGrocery(messId: String, grocery: Grocery): Result<Unit> {
        return try {
            val docRef = firestore.collection(Constants.COLLECTION_MESSES)
                .document(messId)
                .collection(Constants.SUBCOLLECTION_GROCERY)
                .document(grocery.id)

            val doc = GroceryDocument.fromDomain(grocery)
            docRef.set(doc).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteGrocery(messId: String, groceryId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_MESSES)
                .document(messId)
                .collection(Constants.SUBCOLLECTION_GROCERY)
                .document(groceryId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
