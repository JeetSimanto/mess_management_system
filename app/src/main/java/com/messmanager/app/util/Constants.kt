package com.messmanager.app.util

object Constants {
    // Firestore Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_MESSES = "messes"
    const val SUBCOLLECTION_GROCERY = "grocery_entries"
    const val SUBCOLLECTION_UTILITY = "utility_entries"
    const val SUBCOLLECTION_MEAL = "meal_entries"
    const val SUBCOLLECTION_CONTRIBUTION = "contribution_entries"
    const val SUBCOLLECTION_BORROW = "borrow_requests"

    // Constraints & Defaults
    const val INVITE_CODE_LENGTH = 6
    const val MIN_MEALS = 0.0
    const val MAX_MEALS = 3.0
    const val MEAL_STEP = 0.5
}
