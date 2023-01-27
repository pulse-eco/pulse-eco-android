//// Top-level build file where you can add configuration options common to all sub-projects/modules.
//
buildscript {
  repositories {
    google()
    mavenCentral()
    jcenter()

    // Crashlytics
    //maven("https://maven.fabric.io/public")

    maven ("https://dl.bintray.com/kotlin/kotlin-eap")

    maven("./localm2repository")
  }
  dependencies {
    classpath(BuildPlugins.androidGradlePlugin)
    classpath(BuildPlugins.tremaGradlePlugin)
    classpath(BuildPlugins.googlePlayServices)
    classpath(BuildPlugins.kotlinGradlePlugin)
//    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")

    // Crashlytics
    //classpath(BuildPlugins.fabricGradlePlugin)
//    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
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
