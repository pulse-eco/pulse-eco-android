package com.netcetera.skopjepulse.base

import android.content.Context
//import org.jetbrains.anko.defaultSharedPreferences

class PreferredCityStorage(context: Context) {
//  private val storage = context.defaultSharedPreferences

  var cityId: String
    get() = storage.getString(javaClass.simpleName, null) ?: ""
    set(value) = storage.edit().putString(javaClass.simpleName, value).apply()
}