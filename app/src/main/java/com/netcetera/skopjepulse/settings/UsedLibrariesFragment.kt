package com.netcetera.skopjepulse.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceFragmentCompat
import com.netcetera.skopjepulse.R


class UsedLibrariesFragment : PreferenceFragmentCompat(){
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.fragment_libraries, container, false)
  }
  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
// TODO:
  }
}