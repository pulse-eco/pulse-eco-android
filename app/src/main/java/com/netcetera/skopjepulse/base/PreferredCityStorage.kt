package com.netcetera.skopjepulse.base

import android.app.Application
import android.content.Context

class PreferredCityStorage(context: Application) {
  private val storage = context.getSharedPreferences(javaClass.simpleName, Context.MODE_PRIVATE)

  var cityId: String
    get() = storage.getString(javaClass.simpleName, null) ?: ""
    set(value) = storage.edit().putString(javaClass.simpleName, value).apply()
}