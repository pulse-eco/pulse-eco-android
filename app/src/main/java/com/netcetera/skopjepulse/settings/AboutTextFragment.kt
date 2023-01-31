package com.netcetera.skopjepulse.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.main.MainActivity


class AboutTextFragment : PreferenceFragmentCompat() {

   override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
      return inflater.inflate(R.layout.fragment_about_text, container, false)
  }
  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
// TODO:
  }

}
