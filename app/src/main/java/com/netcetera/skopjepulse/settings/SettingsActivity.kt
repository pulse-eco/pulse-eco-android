package com.netcetera.skopjepulse.settings
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.main.MainActivity
import com.netcetera.skopjepulse.main.MainActivity.Companion.popupWindow
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.showConfirmDialog
import com.netcetera.skopjepulse.utils.Internationalisation
import kotlinx.android.synthetic.main.activity_settings_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class SettingsActivity : AppCompatActivity(),
PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
  ,SharedPreferences.OnSharedPreferenceChangeListener{
//
//  val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//  val languagePreference = sharedPreferences.getString("language_preference", "en")

  private val mainViewModel: MainViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)

    btn_back.setOnClickListener {
      onBackPressed()
    }

    if (findViewById<View?>(R.id.idFrameLayout) != null) {
      if (savedInstanceState != null) {
        return
      }

      val newFragment = SettingsFragment()
      supportFragmentManager.beginTransaction()
        .replace(R.id.idFrameLayout, newFragment)
        .commit()
    }
  }

  override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {

    val fragment = pref.key

    if(fragment == "dialog_preference"){
    supportFragmentManager.beginTransaction()
      .replace(R.id.idFrameLayout, AboutTextFragment())
      .addToBackStack(null)
      .commit()
  }   else if(fragment == "library_preference"){
      supportFragmentManager.beginTransaction()
        .replace(R.id.idFrameLayout, UsedLibrariesFragment())
        .addToBackStack(null)
        .commit()
  }   else if(fragment == "disclaimer_preference") {
      supportFragmentManager.beginTransaction()
        .replace(R.id.idFrameLayout, DisclaimerFragment())
        .addToBackStack(null)
        .commit()
    }

    return true
  }

//  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
//    sharedPreferences?.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
//      if (key == "language_preference") {
//        val newLanguage = sharedPreferences.getString(key, "en")
//        if (newLanguage != languagePreference) {
//          recreate()
//        }
//      }
//    }
//  }

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    if (key == "language_preference") {
      val language = sharedPreferences?.getString("language_preference", "")

      if (language == "en") {
        changeLanguage(language!!)
      } else if (language == "mk")
      {
        changeLanguage(language!!)
      }

//      popupWindow.dismiss()
//      showConfirmDialog(this, getString(R.string.change_language_message_android)) {
//        changeLanguage(language!!)
//      }

    }

  }

  private fun changeLanguage(localeName: String) {
    Internationalisation.setLocale(this, localeName)
//    Internationalisation.setLocale(applicationContext, localeName)
    mainViewModel.reloadDDPData()

    finishAndRemoveTask()
//    val intent = Intent(this, MainActivity::class.java)
//    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//    this.startActivity(intent)
  }



//  private fun changeLanguage(lang: String) {
//    val locale = Locale(lang)
//    Locale.setDefault(locale)
//    val config = Configuration()
//    config.locale = locale
//    baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
//
//    recreate()
//  }

}



