plugins {
  id(Plugins.androidApplicationPlugin)
  id(Plugins.kotlinAndroid)
  id(Plugins.kotlinAndroidExtensions)
  id(Plugins.kotlinKapt)
  id(Plugins.fabric)
  id(Plugins.trema)
  id("org.jetbrains.kotlin.android")
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
      // For AGP 4.1+
      // isCoreLibraryDesugaringEnabled = true
      // For AGP 4.0
      coreLibraryDesugaringEnabled = true
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
    implementation(Libs.activityKtx)
    implementation(Libs.koin)
    implementation(Libs.koinScope)
    implementation(Libs.koinViewModel)
    implementation(Libs.timber)
    implementation(Libs.okHttpLogging)
    implementation(Libs.swipeRefreshLayout)

    implementation(Libs.gson)
    //implementation(Libs.desugar)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")


    debugImplementation(Libs.leakcanary)
    debugImplementation(Libs.leakcanaryFragment)
    releaseImplementation(Libs.leakcanaryNoOp)

    kapt(Libs.lifecycleCompiler)
    kapt(Libs.moshiCodegen)
    }
}

tasks.getByPath("preBuild").dependsOn(tasks.named("generateTexts"))

trema {
  defaultLanguage = "en"
  languages = listOf("mk", "en", "de", "ro")
  inputFilePath = file("./build-resources/trema/mobile/translations.trm").absolutePath
}


apply(plugin = "com.google.gms.google-services")
