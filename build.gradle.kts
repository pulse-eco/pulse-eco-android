//// Top-level build file where you can add configuration options common to all sub-projects/modules.
//
buildscript {
  repositories {
    google()
    mavenCentral()
    jcenter()

    // Crashlytics
    maven("https://maven.fabric.io/public")

    maven ("https://dl.bintray.com/kotlin/kotlin-eap")

    maven("./localm2repository")
  }
  dependencies {
    classpath(BuildPlugins.androidGradlePlugin)
    classpath(BuildPlugins.tremaGradlePlugin)
    classpath(BuildPlugins.googlePlayServices)
    classpath(BuildPlugins.kotlinGradlePlugin)

    // Crashlytics
//    classpath(BuildPlugins.fabricGradlePlugin)
  }
  allprojects {
    repositories {
      google()
      mavenCentral()
      jcenter()
      maven("https://jitpack.io")
      maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    }
  }

  task<Delete>("clean") {
    delete(rootProject.buildDir)
  }
}
