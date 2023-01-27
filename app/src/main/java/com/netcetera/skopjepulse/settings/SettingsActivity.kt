package com.netcetera.skopjepulse.settings
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.activity_settings_toolbar.*

class SettingsActivity : AppCompatActivity(),
PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

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
  }   else if(fragment == "disclaimer_preference"){
      supportFragmentManager.beginTransaction()
        .replace(R.id.idFrameLayout, DisclaimerFragment())
        .addToBackStack(null)
        .commit()
  }

    return true
  }

}
