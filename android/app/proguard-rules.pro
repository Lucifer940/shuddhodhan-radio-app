# Radio Shuddhodhan R8/ProGuard rules.
# R8 is currently disabled for the v1.0.1 release; these rules are provided
# for when shrinking is enabled.

# --- Kotlinx Serialization ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.radioshuddhodhan.app.**$$serializer { *; }
-keepclassmembers class com.radioshuddhodhan.app.** { *** Companion; }
-keepclasseswithmembers class com.radioshuddhodhan.app.** { kotlinx.serialization.KSerializer serializer(...); }

# --- Retrofit ---
-keepattributes Signature, Exceptions
-keepclassmembers,allowshrinking,allowobfuscation interface * { @retrofit2.http.* <methods>; }
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# --- OkHttp ---
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# --- Media3 ---
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# --- Firebase messaging ---
-dontwarn com.google.firebase.messaging.**
