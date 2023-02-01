package com.netcetera.skopjepulse.settings
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.main.MainActivity
import com.netcetera.skopjepulse.main.MainActivity.Companion.popupWindow
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.map.MapFragment
import com.netcetera.skopjepulse.showConfirmDialog
import com.netcetera.skopjepulse.utils.Internationalisation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SettingsActivity : AppCompatActivity(),
  PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
  SharedPreferences.OnSharedPreferenceChangeListener {

  private val mainViewModel: MainViewModel by viewModel()
  lateinit var settingsText: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)
    settingsText = findViewById(R.id.settings_text_view_name)
    settingsText.text = "Settings"

    settings_back_button.setOnClickListener {
      onBackPressedDispatcher.onBackPressed()
      changeToolbarText()
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


    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    val language = sharedPreferences.getString("language_preference", "")

    if (language != null && language.isNotEmpty()) {
      changeLanguage(language)
    }

  }

  @Deprecated("Deprecated in Java")
  override fun onBackPressed() {
    super.getOnBackPressedDispatcher().onBackPressed()
    changeToolbarText()
  }

  private fun changeToolbarText() {
    if (!settingsText.equals("Settings")) {
      settingsText.text = "Settings"
    }
  }

  override fun onPreferenceStartFragment(
    caller: PreferenceFragmentCompat,
    pref: Preference
  ): Boolean {
    when (pref.key) {
      "preference_about" -> {
        startFragment(AboutTextFragment())
      }
      "preference_libraries" -> {
        startFragment(UsedLibrariesFragment())
      }
      "preference_disclaimer" -> {
        startFragment(DisclaimerFragment())
      }
    }
    setToolbarName(pref.key)
    return true
  }

  private fun startFragment(fragment: PreferenceFragmentCompat) {
    supportFragmentManager.beginTransaction()
      .replace(R.id.idFrameLayout, fragment)
      .addToBackStack(null)
      .commit()
  }

  //These should be added to the translations.trm sub-module
  private fun setToolbarName(name: String) {
    when (name) {
      "preference_about" -> {
        settingsText.text = "About"
      }
      "preference_libraries" -> {
        settingsText.text = "Libraries"
      }
      "preference_disclaimer" -> {
        settingsText.text = getString(R.string.disclaimer)
      }
    }
  }

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

//    if (key == "language_preference") {
////      val language = sharedPreferences?.getString("language_preference", "")
//      val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//      val language = sharedPreferences.getString("language_preference", "")
//
//      if (language == "en") {
//        changeLanguage(language!!)
//      } else if (language == "mk")
//      {
//        changeLanguage(language!!)
//      }

//      popupWindow.dismiss()
//      showConfirmDialog(this, getString(R.string.change_language_message_android)) {
//        changeLanguage(language!!)
//      }
//
//    }
//
  }

//  private fun changeLanguage(localeName: String) {
//    Internationalisation.setLocale(this, localeName)
//    Internationalisation.setLocale(applicationContext, localeName)
//    mainViewModel.reloadDDPData()
//
//    val intent = Intent(this, MainActivity::class.java)
//    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//    this.startActivity(intent)
//  }
//

  private fun changeLanguage(localeName: String) {
    Internationalisation.setLocale(this, localeName)
//    Internationalisation.setLocale(applicationContext, localeName)
    mainViewModel.reloadDDPData()


    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
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



