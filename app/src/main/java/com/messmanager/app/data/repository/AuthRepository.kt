package com.messmanager.app.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.messmanager.app.data.remote.model.UserDocument
import com.messmanager.app.domain.model.User
import com.messmanager.app.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUserId: String?
        get() = auth.currentUser?.uid

    val isAuthenticated: Boolean
        get() = auth.currentUser != null

    fun observeCurrentUser(): Flow<User?> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(Constants.COLLECTION_USERS)
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val userDoc = snapshot?.toObject(UserDocument::class.java)
                trySend(userDoc?.toDomain())
            }

        awaitClose { listener.remove() }
    }

    suspend fun signInWithCredential(credential: AuthCredential): Result<User> {
        return try {
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Sign in failed: user is null"))

            val userRef = firestore.collection(Constants.COLLECTION_USERS).document(firebaseUser.uid)
            val docSnapshot = userRef.get().await()

            val user = if (docSnapshot.exists()) {
                val existingDoc = docSnapshot.toObject(UserDocument::class.java)!!
                // Update profile info in case it changed
                val updatedDoc = existingDoc.copy(
                    displayName = firebaseUser.displayName ?: existingDoc.displayName,
                    email = firebaseUser.email ?: existingDoc.email,
                    photoUrl = firebaseUser.photoUrl?.toString() ?: existingDoc.photoUrl
                )
                userRef.set(updatedDoc).await()
                updatedDoc.toDomain()
            } else {
                val newDoc = UserDocument(
                    uid = firebaseUser.uid,
                    displayName = firebaseUser.displayName ?: "User",
                    email = firebaseUser.email ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                    messIds = emptyList(),
                    activeMessId = null
                )
                userRef.set(newDoc).await()
                newDoc.toDomain()
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateActiveMess(messId: String?): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(Exception("User not authenticated"))
        return try {
            firestore.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .update("activeMessId", messId)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFcmToken(token: String): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(Exception("User not authenticated"))
        return try {
            firestore.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .update("fcmToken", token)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
