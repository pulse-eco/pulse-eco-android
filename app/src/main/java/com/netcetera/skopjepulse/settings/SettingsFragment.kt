package com.netcetera.skopjepulse.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.netcetera.skopjepulse.R


class SettingsFragment : PreferenceFragmentCompat() {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences, rootKey)
  }

}

