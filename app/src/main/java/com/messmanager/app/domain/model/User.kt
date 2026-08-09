package com.messmanager.app.domain.model

data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val messIds: List<String> = emptyList(),
    val activeMessId: String? = null
)
