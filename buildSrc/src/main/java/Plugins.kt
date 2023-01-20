object ProjectVersions {
  const val androidGradlePlugin = "4.2.2"
  const val googlePlayServices = "4.3.2"
  const val kotlin = "1.5.32"
  const val trema = "2.1.9"
}

object BuildPlugins {
  const val androidGradlePlugin = "com.android.tools.build:gradle:${ProjectVersions.androidGradlePlugin}"
  const val googlePlayServices = "com.google.gms:google-services:${ProjectVersions.googlePlayServices}"
  const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${ProjectVersions.kotlin}"
  const val tremaGradlePlugin = "com.netcetera.android.gradle:trema-gradle-plugin:${ProjectVersions.trema}"
}

object Plugins {
  const val androidApplicationPlugin = "com.android.application"
  const val kotlinAndroid = "kotlin-android"
  const val kotlinAndroidExtensions = "kotlin-android-extensions"
  const val kotlinKapt = "kotlin-kapt"
  const val trema = "com.netcetera.trema"
}