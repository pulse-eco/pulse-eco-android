package com.netcetera.skopjepulse.base

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.core.content.PermissionChecker
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.netcetera.skopjepulse.BuildConfig
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.di.appModule
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import java.util.*

class App : Application() {

  override fun onCreate() {
    super.onCreate()
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return
    }

    setupKoin()
    setupTimber()
    setupCrashlytics()
    setupLocale()
  }

  private fun setupLocale(){
    val lang = getSharedPreferences(Constants.LANGUAGE_CODE, Context.MODE_PRIVATE).getString(Constants.LANGUAGE_CODE, "")

    if(lang!!.isEmpty()){
      val editor: SharedPreferences.Editor = getSharedPreferences(Constants.LANGUAGE_CODE, Context.MODE_PRIVATE).edit()
      editor.putString(Constants.LANGUAGE_CODE, Locale.getDefault().language)
      editor.apply()
    }
  }

  private fun setupKoin() {
    startKoin {
      androidContext(this@App)
      modules(appModule)
    }
  }

  private fun setupTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }

  private fun setupCrashlytics() {
    Fabric.with(this,
        Crashlytics.Builder()
            .core(CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build())
            .build()
    )
  }

}

fun Context.isOnline(): Boolean {
  val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  return connectivityManager.activeNetworkInfo?.isConnected ?: false
}

fun Context.isLocationPermissionGranted(): Boolean {
  val coarse = PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED
  val fine = PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED
  return coarse && fine
}