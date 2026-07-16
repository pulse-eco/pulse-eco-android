// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  repositories {
    google()
    mavenCentral()
    maven { url = uri("$rootDir/localm2repository") }
  }
  dependencies {
    classpath(BuildPlugins.androidGradlePlugin)
    classpath(BuildPlugins.tremaGradlePlugin)
    classpath(BuildPlugins.googlePlayServices)
    classpath(BuildPlugins.kotlinGradlePlugin)

    // Crashlytics
    classpath(BuildPlugins.firebaseCrashlyticsGradlePlugin)
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
    maven { url = uri("$rootDir/localm2repository") }
  }
}

tasks.register<Delete>("clean") {
  delete(rootProject.buildDir)
}
