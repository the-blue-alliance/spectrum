# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/nathan/Android/Sdk/tools/proguard/proguard-android.txt
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

# From http://stackoverflow.com/a/31567519/3995632
# Ensure annotations are kept for runtime use.
-keepattributes *Annotation*
# Don't remove any GreenRobot classes
-keep class org.greenrobot.** {*;}
# Don't remove any methods that have the @Subscribe annotation
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
