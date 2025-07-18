# --- General Kotlin metadata ---
-keepattributes *Annotation*
-keepattributes Signature
-keep class kotlin.** { *; }
-keepclassmembers class kotlin.Metadata { *; }

# --- Jetpack Compose (UI and animation) ---
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# For Material3
-keep class androidx.compose.material3.** { *; }

# --- Retrofit (models & API interfaces) ---
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keep class com.example.material.api.** { *; }

# GSON (needed for Retrofit + Gson converter)
-keep class com.google.gson.** { *; }
-keep class com.example.material.model.** { *; }

# Retrofit callbacks & converters
-dontwarn okhttp3.**
-dontwarn retrofit2.converter.gson.**
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}

# --- Hilt / Dagger ---
-dontwarn dagger.hilt.internal.**
-keep class dagger.hilt.** { *; }
-keep interface dagger.hilt.** { *; }

# Needed for generated Hilt classes
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory { *; }
-keep class androidx.hilt.** { *; }

# ViewModels and Hilt-injected constructors
-keep class * extends androidx.lifecycle.ViewModel
-keep class * {
    @dagger.hilt.android.lifecycle.HiltViewModel *;
}

# --- Navigation Compose ---
-keep class androidx.navigation.** { *; }
-keepclassmembers class * {
    @androidx.navigation.* <methods>;
}

# --- DataStore ---
-keep class androidx.datastore.** { *; }
-keepclassmembers class androidx.datastore.** { *; }

# --- Accompanist ---
-keep class com.google.accompanist.** { *; }

# --- Optional: Suppress Compose previews from obfuscation ---
-keep class **_PreviewKt { *; }

# --- Logging (optional, for OkHttp) ---
-dontwarn okio.**
-dontwarn javax.annotation.**
# --- Fix potential Retrofit + GSON model stripping ---
-keepclassmembers class com.example.material.api.** {
    <fields>;
    public <init>();
}

# --- Keep Retrofit service interfaces fully intact ---
-keep interface com.example.material.api.** { *; }

# --- Keep Hilt ViewModels and constructors ---
-keep class com.example.material.viewmodel.** { *; }
-keepclassmembers class com.example.material.viewmodel.** {
    <init>(...);
}
# --- Firebase Cloud Messaging (FCM) ---
-keep class com.google.firebase.messaging.** { *; }
-keep class com.google.firebase.iid.** { *; }  # older fallback for some devices
-keep class com.google.firebase.installations.** { *; }
-keep class com.google.android.gms.tasks.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.android.gms.base.** { *; }

# --- Firebase Core Services ---
-keep class com.google.firebase.components.** { *; }
-keep class com.google.firebase.provider.** { *; }
-keep class com.google.firebase.** { *; }
-keep class com.google.android.datatransport.** { *; }
-keep class com.google.dagger.** { *; }

# --- Avoid removing Firebase Init & Auto startup ---
-keep class com.google.firebase.FirebaseApp { *; }
-keep class com.google.firebase.platforminfo.** { *; }
-keep class com.google.firebase.heartbeatinfo.** { *; }

# --- Prevent stripping FCM service declarations ---
-keep class com.google.firebase.messaging.FirebaseMessagingService { *; }

# --- Prevent logging & analytics stripping ---
-dontwarn com.google.firebase.messaging.**
-dontwarn com.google.firebase.installations.**
-dontwarn com.google.firebase.iid.**
-dontwarn com.google.android.datatransport.**

-keep class com.example.material.DeviceTokenRequest { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# -- keep the Importance enum and its constant names -----------------
-keepnames enum com.example.material.pages.commons.Importance
-keepclassmembers enum com.example.material.pages.commons.Importance { *; }

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.slf4j.impl.StaticMDCBinder
-dontwarn org.slf4j.impl.StaticMarkerBinder

# Keep your ChatMessage class with all fields
-keep class com.example.material.pages.commons.ChatMessage { *; }

# Keep all model classes if you deserialize manually
-keepclassmembers class * {
    <init>(...);
}

# Optional: if you use JSONObject a lot
-keepnames class org.json.** { *; }
