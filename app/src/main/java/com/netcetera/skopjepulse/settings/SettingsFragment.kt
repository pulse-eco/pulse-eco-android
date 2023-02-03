package com.netcetera.skopjepulse.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.netcetera.skopjepulse.R

class SettingsFragment : PreferenceFragmentCompat() {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    (activity as AppCompatActivity).supportActionBar?.title = "Settings"
    setPreferencesFromResource(R.xml.preferences, rootKey)

  }

}
