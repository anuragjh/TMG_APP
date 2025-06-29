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
