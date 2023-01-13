package com.netcetera.skopjepulse.settings

import android.os.Bundle
import android.preference.PreferenceFragment
import com.netcetera.skopjepulse.R

class SettingsFragment : PreferenceFragment() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addPreferencesFromResource(R.xml.preferences)
  }
}