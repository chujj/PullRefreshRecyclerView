# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/zhujj/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepattributes SourceFile,LineNumberTable
-verbose

-keep class * extends android.support.v4.app.Fragment
-keep class android.support.v4.view.ViewPager.** { *; }
-keep class * extends android.support.v4.view.ViewPager { *; }
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider
-keep class * extends android.app.Application
-keep class * extends android.support.v4.view.ActionProvider { *; }



-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}


## mine
-keep class * extends com.biaoyixin.shangcheng.model.BaseModel {*;}
-keep class com.biaoyixin.shangcheng.account.** { *; }

## eventbus
-keepclassmembers class ** {
    public void onEvent*(**);
}


## glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}



## retrofit1.x https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-square-retrofit.pro
-keep class com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn retrofit.**
-dontwarn rx.**

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

# If in your rest service interface you use methods with Callback argument.
-keepattributes Exceptions

# If your rest service methods throw custom exceptions, because you've defined an ErrorHandler.
-keepattributes Signature

# Also you must note that if you are using GSON for conversion from JSON to POJO representation, you must ignore those POJO classes from being obfuscated.
# Here include the POJO's that have you have created for mapping JSON response to POJO for example.