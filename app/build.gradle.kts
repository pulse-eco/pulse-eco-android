plugins {
  id("com.android.application")
  id(Plugins.kotlinAndroid)
  id(Plugins.kotlinKapt)
  id("kotlin-parcelize")
//  id(Plugins.kotlinKsp)
  id(Plugins.trema)
}

android {
  compileSdkVersion(ProjectProperties.compileSdkVersion)
  defaultConfig {
    applicationId = PulseEco.applicationId
    minSdkVersion(ProjectProperties.minimumSdkVersion)
    targetSdkVersion(ProjectProperties.targetSdkVersion)
    versionCode = PulseEco.versionCode
    versionName = PulseEco.versionName
    multiDexEnabled = true
  }
  buildTypes {
    getByName("release") {
      isDebuggable = false
      isMinifyEnabled = true
      manifestPlaceholders["googleMapsApiKey"] = ProjectSecrets.googleMapsReleaseApiKey
      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "proguard-rules.pro",
        "proguard-square-okhttp3.pro",
        "proguard-square-okio.pro",
        "proguard-square-moshi.pro",
        "proguard-support-design.pro",
        "proguard-support-v7-appcompat.pro",
        "proguard-google-play-services.pro",
        "proguard-google-architecture-components.pro",
        "proguard-mpandroidchart.pro"
      )
    }
    getByName("debug") {
      manifestPlaceholders["googleMapsApiKey"] = ProjectSecrets.googleMapsDebugApiKey
    }
    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
  }

  dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.21")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.annotation:annotation:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime:2.5.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi:1.11.0")
    implementation("com.squareup.moshi:moshi-adapters:1.11.0")
//    implementation("org.jetbrains.anko:anko-commons:0.10.8")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.firebase:firebase-analytics:21.2.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.airbnb.android:lottie:3.5.0")
    implementation("com.github.jd-alexander:LikeButton:0.2.3")
    implementation("it.sephiroth.android.library.targettooltip:target-tooltip-library:2.0.3")
    implementation("androidx.activity:activity-ktx:1.6.1")
//    implementation("org.koin:koin-android:2.2.2")
    implementation("io.insert-koin:koin-android:2.2.2")
//    implementation("org.koin:koin-androidx-scope:2.2.2")
    implementation("io.insert-koin:koin-androidx-scope:2.2.2")
//    implementation("org.koin:koin-androidx-view-model:2.2.2")
    implementation("io.insert-koin:koin-androidx-viewmodel:2.2.2")
    implementation("com.github.ajalt:timberkt:1.5.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
//    implementation("androidx.relativelayouts:relativelayouts:1.1.0")
    implementation("com.google.code.gson:gson:2.10")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.0")
//    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.10")
//    debugImplementation("com.squareup.leakcanary:leakcanary-support-fragment:2.10")
//    releaseImplementation("com.squareup.leakcanary:leakcanary-android-no-op:2.10")
    implementation("com.squareup.leakcanary:leakcanary-android:2.10")

    implementation("androidx.lifecycle:lifecycle-common-java8:2.5.1")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.11.0")
    }
}

tasks.getByPath("preBuild").dependsOn(tasks.named("generateTexts"))

trema {
  defaultLanguage = "en"
  languages = listOf("mk", "en", "de", "ro")
  inputFilePath = file("./build-resources/trema/mobile/translations.trm").absolutePath
}


apply(plugin = "com.google.gms.google-services")
