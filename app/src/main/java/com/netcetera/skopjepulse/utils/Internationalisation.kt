package com.netcetera.skopjepulse.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import com.netcetera.skopjepulse.Constants
import java.util.*

class Internationalisation {
  companion object {
    fun setLocale(context: Context, language: String){
      val locale = Locale(language)
      Locale.setDefault(locale)
      val resources: Resources = context.resources
      val dm: DisplayMetrics = resources.displayMetrics
      val config: Configuration = resources.configuration
      config.setLocale(Locale(language!!.toLowerCase(Locale.ROOT)))

      resources.updateConfiguration(config, dm)
      val editor = context.getSharedPreferences(Constants.LANGUAGE_CODE, Context.MODE_PRIVATE).edit()
      editor.putString(Constants.LANGUAGE_CODE, language)
      editor.apply()
    }

    fun loadLocale(context: Context){
      val prefs = context.getSharedPreferences(Constants.LANGUAGE_CODE, Context.MODE_PRIVATE)
      val language = prefs.getString(Constants.LANGUAGE_CODE, "")
      if(language != null)
        setLocale(context, language)
    }
  }
}