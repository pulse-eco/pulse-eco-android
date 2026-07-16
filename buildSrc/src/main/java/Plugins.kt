object ProjectVersions {
  const val androidGradlePlugin = "7.4.2"
  const val googlePlayServices = "4.3.15"
  const val kotlin = "1.7.21"
  const val trema = "2.1.9"

  // Crashlytics
  const val firebaseCrashlyticsGradle = "2.9.9"
}

object BuildPlugins {
  const val androidGradlePlugin = "com.android.tools.build:gradle:${ProjectVersions.androidGradlePlugin}"
  const val googlePlayServices = "com.google.gms:google-services:${ProjectVersions.googlePlayServices}"
  const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${ProjectVersions.kotlin}"
  const val tremaGradlePlugin = "com.netcetera.android.gradle:trema-gradle-plugin:${ProjectVersions.trema}"

  // Crashlytics
  const val firebaseCrashlyticsGradlePlugin = "com.google.firebase:firebase-crashlytics-gradle:${ProjectVersions.firebaseCrashlyticsGradle}"
}

object Plugins {
  const val androidApplicationPlugin = "com.android.application"
  const val kotlinAndroid = "kotlin-android"
  const val kotlinAndroidExtensions = "kotlin-android-extensions"
  const val kotlinKapt = "kotlin-kapt"
  const val trema = "com.netcetera.trema"

  // Crashlytics
  const val firebaseCrashlytics = "com.google.firebase.crashlytics"
}