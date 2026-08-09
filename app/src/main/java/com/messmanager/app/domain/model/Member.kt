package com.messmanager.app.domain.model

data class Member(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val role: MessRole = MessRole.MEMBER
)
