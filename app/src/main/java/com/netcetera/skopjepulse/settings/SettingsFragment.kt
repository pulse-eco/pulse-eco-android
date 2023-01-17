package com.netcetera.skopjepulse.settings
import android.app.AlertDialog
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import com.netcetera.skopjepulse.R

class SettingsFragment : PreferenceFragment() {

  val builder: AlertDialog.Builder? = activity?.let {
    AlertDialog.Builder(it)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addPreferencesFromResource(R.xml.preferences)
    builder?.setMessage(R.string.aboutText)
      ?.setTitle("About")

    val dialogPreference = preferenceScreen.findPreference("dialog_preference") as Preference
    dialogPreference.setOnPreferenceClickListener(object : Preference.OnPreferenceClickListener {
      override fun onPreferenceClick(preference: Preference): Boolean {
        val dialog: AlertDialog? = builder?.create()
        return true
      }
    })
  }
}