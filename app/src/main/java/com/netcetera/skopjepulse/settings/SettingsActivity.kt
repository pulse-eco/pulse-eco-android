package com.netcetera.skopjepulse.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.main.MainActivity
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.utils.Internationalisation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import java.util.*

class SettingsActivity : AppCompatActivity(),
  PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
  SharedPreferences.OnSharedPreferenceChangeListener {

  private val mainViewModel: MainViewModel by viewModel()
  lateinit var settingsText: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)
    PreferenceManager.getDefaultSharedPreferences(this)
      .registerOnSharedPreferenceChangeListener(this)

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

      startFragment(SettingsFragment(), true)
    }
  }

  @Deprecated("Deprecated in Java")
  override fun onBackPressed() {
    super.getOnBackPressedDispatcher().onBackPressed()
    changeToolbarText()
  }

  override fun onDestroy() {
    super.onDestroy()
    PreferenceManager.getDefaultSharedPreferences(this)
      .unregisterOnSharedPreferenceChangeListener(this)
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
        startFragment(AboutTextFragment(), false)
      }
      "preference_libraries" -> {
        startFragment(UsedLibrariesFragment(), false)
      }
      "preference_disclaimer" -> {
        startFragment(DisclaimerFragment(), false)
      }
    }
    setToolbarName(pref.key)
    return true
  }

  private fun startFragment(
    preferenceFragmentCompat: PreferenceFragmentCompat,
    addToBackStack: Boolean
  ) {
    val fragment = supportFragmentManager.beginTransaction()
      .replace(R.id.idFrameLayout, preferenceFragmentCompat)
    if (!addToBackStack) {
      fragment.addToBackStack(null)
    }
      fragment.commit()
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
    when (key) {
      "language_preference" -> {
        val lan = sharedPreferences?.getString("language_preference", "")
        if (lan != null && lan.isNotEmpty()) {
          changeLanguage(lan)
        }
      }
    }
  }

  @OptIn(KoinApiExtension::class)
  private fun changeLanguage(localeName: String) {
    Internationalisation.setLocale(this, localeName)
    Internationalisation.setLocale(applicationContext, localeName)
    mainViewModel.reloadDDPData()

    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
  }
}

