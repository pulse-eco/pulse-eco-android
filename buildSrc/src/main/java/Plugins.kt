object ProjectVersions {
  const val androidGradlePlugin = "3.5.2"
  const val googlePlayServices = "4.3.2"
  const val kotlin = "1.3.61"

  // Crashlytics
  const val fabric = "1.31.2"
}

object BuildPlugins {
  const val androidGradlePlugin = "com.android.tools.build:gradle:${ProjectVersions.androidGradlePlugin}"
  const val googlePlayServices = "com.google.gms:google-services:${ProjectVersions.googlePlayServices}"
  const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${ProjectVersions.kotlin}"

  // Crashlytics
  const val fabricGradlePlugin = "io.fabric.tools:gradle:${ProjectVersions.fabric}"
}

object Plugins {
  const val androidApplicationPlugin = "com.android.application"
  const val kotlinAndroid = "kotlin-android"
  const val kotlinAndroidExtensions = "kotlin-android-extensions"
  const val kotlinKapt = "kotlin-kapt"

  // Crashlytics
  const val fabric = "io.fabric"
}