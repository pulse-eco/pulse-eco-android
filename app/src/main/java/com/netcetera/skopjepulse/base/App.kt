package com.netcetera.skopjepulse.base

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.core.content.PermissionChecker
import com.netcetera.skopjepulse.BuildConfig
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import java.util.*

class App : Application() {

  override fun onCreate() {
    super.onCreate()
    setupKoin()
    setupTimber()
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