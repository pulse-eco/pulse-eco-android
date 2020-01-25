plugins {
  id(Plugins.androidApplicationPlugin)
  id(Plugins.kotlinAndroid)
  id(Plugins.kotlinAndroidExtensions)
  id(Plugins.kotlinKapt)
  id(Plugins.fabric)
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
    androidExtensions {
      isExperimental = true
    }
  }

  dependencies {
    implementation(Libs.kotlinStdlib)
    implementation(Libs.androidX)
    implementation(Libs.recyclerView)
    implementation(Libs.androidMaterial)
    implementation(Libs.androidXAnnotations)
    implementation(Libs.constaintLayout)
    implementation(Libs.lifecycleRuntime)
    implementation(Libs.lifecycleExtensions)
    implementation(Libs.retrofit)
    implementation(Libs.retrofitConverterMoshi)
    implementation(Libs.moshi)
    implementation(Libs.moshiAdapter)
    implementation(Libs.anko)
    implementation(Libs.googleMaps)
    implementation(Libs.playServicesLocation)
    implementation(Libs.firebaseAnalytics)
    implementation(Libs.crashlytics)
    implementation(Libs.mpaAndroidChart)
    implementation(Libs.lottie)
    implementation(Libs.likeButton)
    implementation(Libs.targetTooltip)
    implementation(Libs.koin)
    implementation(Libs.koinScope)
    implementation(Libs.koinViewModel)
    implementation(Libs.timber)
    implementation(Libs.okHttpLogging)

    debugImplementation(Libs.leakcanary)
    debugImplementation(Libs.leakcanaryFragment)
    releaseImplementation(Libs.leakcanaryNoOp)

    kapt(Libs.lifecycleCompiler)
    kapt(Libs.moshiCodegen)
    }
}
apply(plugin = "com.google.gms.google-services")
