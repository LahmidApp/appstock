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

# Apache POI rules - Non utilisé maintenant (remplacé par CSV natif)
# -dontwarn org.apache.batik.**
# -dontwarn org.apache.logging.log4j.**
# -dontwarn org.apache.xmlbeans.**
# -dontwarn org.apache.commons.logging.**
# -dontwarn javax.xml.stream.**
# -dontwarn org.w3c.dom.**
# -dontwarn org.openxmlformats.**

# Keep Apache POI classes - Non utilisé maintenant
# -keep class org.apache.poi.** { *; }
# -keep class org.apache.xmlbeans.** { *; }

# Keep Room classes
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Keep ZXing classes  
-keep class com.google.zxing.** { *; }
-keep class com.journeyapps.barcodescanner.** { *; }

# Native Android PDF generation (no external dependencies needed)
# Previously used iTextPDF but replaced with android.graphics.pdf.PdfDocument

# Keep all serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keep class androidx.activity.compose.** { *; }
-keep class androidx.navigation.compose.** { *; }