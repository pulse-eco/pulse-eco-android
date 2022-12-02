//// Top-level build file where you can add configuration options common to all sub-projects/modules.
//
buildscript {
  repositories {
    google()
    mavenCentral()
    maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("./localm2repository")
  }
  dependencies {
    classpath(BuildPlugins.androidGradlePlugin)
    classpath(BuildPlugins.googlePlayServices)
    classpath(BuildPlugins.kotlinGradlePlugin)
    classpath(BuildPlugins.tremaGradlePlugin)
  }
  allprojects {
    repositories {
      google()
      mavenCentral()
      maven("https://jitpack.io")
      maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    }
  }

  task<Delete>("clean") {
    delete(rootProject.buildDir)
  }
}
