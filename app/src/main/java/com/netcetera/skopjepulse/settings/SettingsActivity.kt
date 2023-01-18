package com.netcetera.skopjepulse.settings
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.utils.Internationalisation
import kotlinx.android.synthetic.main.activity_settings_toolbar.*

class SettingsActivity : AppCompatActivity() ,
  PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)
    Internationalisation.loadLocale(this)


    btn_back.setOnClickListener {
      finish()
      onBackPressed()
    }

    if (findViewById<View?>(R.id.idFrameLayout) != null) {
      if (savedInstanceState != null) {
        return
      }
      fragmentManager.beginTransaction().add(R.id.idFrameLayout, SettingsFragment()).commit()
    }
  }

  override fun onPreferenceStartFragment(
    caller: PreferenceFragmentCompat,
    pref: Preference
  ): Boolean {
    val args = pref.extras
    val fragment = supportFragmentManager.fragmentFactory.instantiate(
      classLoader,
      pref.fragment!!
    )
    fragment.arguments = args
    fragment.setTargetFragment(caller, 0)

    supportFragmentManager.beginTransaction()
      .replace(R.id.idFrameLayout, AboutTextFragment())
      .addToBackStack(null)
      .commit()
    return true
  }

//  override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
//
//    val args = pref.extras
//    val fragment = supportFragmentManager.fragmentFactory.instantiate(
//      classLoader,
//      pref.fragment!!
//    )
//    fragment.arguments = args
//    fragment.setTargetFragment(caller, 0)
//
//    supportFragmentManager.beginTransaction()
//      .replace(R.id.idFrameLayout, AboutTextFragment())
//      .addToBackStack(null)
//      .commit()
//    return true
//  }


}

