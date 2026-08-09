# Add project specific ProGuard rules here.

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Firestore document models
-keep class com.messmanager.app.data.remote.model.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase { *; }

# Coroutines
-keepnames class kotlinx.coroutines.** { *; }
