# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class com.google.** { *; }
-keep class com.android.** { *; }
-keep class android.support.v7.** { *; }
-keep class java.lang.** { *; }

-dontwarn android.support.v7.**

-keepattributes Signature
-keepattributes *Annotation*

# Remove Logging
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int e(...);
    public static int w(...);
    public static int wtf(...);
    public static int d(...);
    public static int v(...);
    public static int i(...);
}

#Encrpted Shared Pref
-keep class com.google.crypto.** { *; }
#-keepclassmembers class * extends com.google.crypto.tink.shaded.protobuf.GeneratedMessageLite {
#  <fields>;
#}

-keep class com.android.app.data.model.* { *; }

-keep class com.android.app.data.repositories.article.request.* { *; }
-keep class com.android.app.data.repositories.article.response.* { *; }

-keep class com.android.app.data.repositories.auth.request.* { *; }
-keep class com.android.app.data.repositories.auth.response.* { *; }