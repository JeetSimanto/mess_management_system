package com.messmanager.app.data.remote.model

import com.google.firebase.firestore.ServerTimestamp
import com.messmanager.app.domain.model.User
import java.util.Date

data class UserDocument(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val messIds: List<String> = emptyList(),
    val activeMessId: String? = null,
    @ServerTimestamp val updatedAt: Date? = null
) {
    fun toDomain(): User = User(
        uid = uid,
        displayName = displayName,
        email = email,
        photoUrl = photoUrl,
        messIds = messIds,
        activeMessId = activeMessId
    )

    companion object {
        fun fromDomain(user: User): UserDocument = UserDocument(
            uid = user.uid,
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl,
            messIds = user.messIds,
            activeMessId = user.activeMessId
        )
    }
}
