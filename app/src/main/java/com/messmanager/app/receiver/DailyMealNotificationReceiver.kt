package com.messmanager.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.messmanager.app.data.local.DailyNotificationPreferences
import com.messmanager.app.data.remote.model.MealDocument
import com.messmanager.app.data.remote.model.UserDocument
import com.messmanager.app.util.Constants
import com.messmanager.app.util.NotificationHelper
import com.messmanager.app.util.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.Calendar

class DailyMealNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val prefs = DailyNotificationPreferences(context)
        
        // Immediately reschedule for the next day to maintain continuous daily loop
        if (prefs.isEnabled) {
            NotificationScheduler.scheduleDailyNotification(context, prefs.hour, prefs.minute)
        }

        if (!prefs.isEnabled) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUid = FirebaseAuth.getInstance().currentUser?.uid
                if (currentUid == null) {
                    pendingResult.finish()
                    return@launch
                }

                val db = FirebaseFirestore.getInstance()
                
                // Fetch User Document to get active mess
                val userDoc = db.collection(Constants.COLLECTION_USERS)
                    .document(currentUid)
                    .get()
                    .await()
                    .toObject(UserDocument::class.java)

                val activeMessId = userDoc?.activeMessId
                if (activeMessId.isNullOrEmpty()) {
                    pendingResult.finish()
                    return@launch
                }

                // Get Current Month & Year
                val now = LocalDate.now()
                val month = now.monthValue
                val year = now.year

                // Fetch User's Meals for Current Month
                val mealsSnapshot = db.collection(Constants.COLLECTION_MESSES)
                    .document(activeMessId)
                    .collection(Constants.SUBCOLLECTION_MEAL)
                    .whereEqualTo("memberUid", currentUid)
                    .whereEqualTo("month", month)
                    .whereEqualTo("year", year)
                    .get()
                    .await()

                val meals = mealsSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(MealDocument::class.java)
                }

                val totalMeals: Double = meals.sumOf { it.count }

                // Format string e.g. "You have consumed a total of 42.5 Meals"
                val formattedTotal = if (totalMeals % 1.0 == 0.0) {
                    totalMeals.toInt().toString()
                } else {
                    String.format(java.util.Locale.US, "%.1f", totalMeals)
                }

                val bodyText = "You have consumed a total of $formattedTotal Meals"
                NotificationHelper.showMealSummaryNotification(context, bodyText)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
