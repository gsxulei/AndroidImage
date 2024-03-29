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

#-ignorewarnings

#MsgBus混淆
-keepclassmembers class commons.msgbus.MsgReceiver
#-keep enum commons.msgbus.MsgThread{*;}
-keepclassmembers class **{
    @commons.msgbus.MsgReceiver <methods>;
}

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#OKHTTP
-dontwarn com.squareup.okhttp3.**
-dontwarn okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**
# JSR 305 annotations are for embedding nullability information.
#-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
#-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
#-dontwarn org.codehaus.mojo.animal_sniffer.*
# OkHttp platform used only on JVM and when Conscrypt dependency is available.
#-dontwarn okhttp3.internal.platform.ConscryptPlatform

#Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

#自定义混淆字典

#指定外部模糊字典
#-obfuscationdictionary proguard/p0005.txt
#指定class模糊字典
#-classobfuscationdictionary proguard/p0005.txt
#指定package模糊字典
#-packageobfuscationdictionary proguard/p0005.txt

#关闭混淆，仅保留压缩和优化
#-dontobfuscate

#关闭混淆，仅保留压缩和优化
#-dontobfuscate