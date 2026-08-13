package com.messmanager.app.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.messmanager.app.data.remote.model.MessDocument
import com.messmanager.app.domain.model.Mess
import com.messmanager.app.util.Constants
import com.messmanager.app.util.InviteCodeGenerator
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun createMess(
        name: String,
        month: Int,
        year: Int,
        userId: String
    ): Result<Mess> {
        return try {
            val messRef = firestore.collection(Constants.COLLECTION_MESSES).document()
            val inviteCode = InviteCodeGenerator.generateCode()

            val messDoc = MessDocument(
                id = messRef.id,
                name = name,
                inviteCode = inviteCode,
                managerId = userId,
                month = month,
                year = year,
                memberIds = listOf(userId)
            )

            // Batch write: create mess document and update user's messIds + activeMessId
            val batch = firestore.batch()
            batch.set(messRef, messDoc)

            val userRef = firestore.collection(Constants.COLLECTION_USERS).document(userId)
            batch.update(userRef, "messIds", FieldValue.arrayUnion(messRef.id))
            batch.update(userRef, "activeMessId", messRef.id)

            batch.commit().await()
            Result.success(messDoc.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinMess(inviteCode: String, userId: String): Result<Mess> {
        return try {
            val querySnapshot = firestore.collection(Constants.COLLECTION_MESSES)
                .whereEqualTo("inviteCode", inviteCode.uppercase().trim())
                .limit(1)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                return Result.failure(Exception("Invalid invite code. Please check and try again."))
            }

            val messDoc = querySnapshot.documents.first().toObject(MessDocument::class.java)!!
            
            if (messDoc.memberIds.contains(userId)) {
                // User already in mess, just set active
                val userRef = firestore.collection(Constants.COLLECTION_USERS).document(userId)
                userRef.update("activeMessId", messDoc.id).await()
                return Result.success(messDoc.toDomain())
            }

            if (messDoc.memberIds.size >= 20) {
                return Result.failure(Exception("This mess has reached its maximum member limit (20)."))
            }

            // Batch write: add user to mess memberIds, update user document
            val batch = firestore.batch()
            val messRef = firestore.collection(Constants.COLLECTION_MESSES).document(messDoc.id)
            val userRef = firestore.collection(Constants.COLLECTION_USERS).document(userId)

            batch.update(messRef, "memberIds", FieldValue.arrayUnion(userId))
            batch.update(userRef, "messIds", FieldValue.arrayUnion(messDoc.id))
            batch.update(userRef, "activeMessId", messDoc.id)

            batch.commit().await()

            val updatedMess = messDoc.copy(memberIds = messDoc.memberIds + userId)
            Result.success(updatedMess.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeMess(messId: String): Flow<Mess?> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_MESSES)
            .document(messId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val doc = snapshot?.toObject(MessDocument::class.java)
                trySend(doc?.toDomain())
            }
        awaitClose { listener.remove() }
    }

    suspend fun transferManager(messId: String, newManagerId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_MESSES)
                .document(messId)
                .update("managerId", newManagerId)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeMember(messId: String, memberId: String): Result<Unit> {
        return try {
            val batch = firestore.batch()
            val messRef = firestore.collection(Constants.COLLECTION_MESSES).document(messId)
            val userRef = firestore.collection(Constants.COLLECTION_USERS).document(memberId)

            batch.update(messRef, "memberIds", FieldValue.arrayRemove(memberId))
            batch.update(userRef, "messIds", FieldValue.arrayRemove(messId))

            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun leaveMess(messId: String, userId: String): Result<Unit> {
        return removeMember(messId, userId)
    }
}
