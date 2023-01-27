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
      finish()
      onBackPressed()
    }

    if (findViewById<View?>(R.id.idFrameLayout) != null) {
      if (savedInstanceState != null) {
        return
      }
      fragmentManager.beginTransaction().replace(R.id.idFrameLayout, SettingsFragment()).commit()
    }
  }

  override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
    // Instantiate the new Fragment
    val args = pref.extras
    val fragment = supportFragmentManager.fragmentFactory.instantiate(
      classLoader,
      pref.fragment!!)
    fragment.arguments = args
    fragment.setTargetFragment(caller, 0)
    // Replace the existing Fragment with the new Fragment
    supportFragmentManager.beginTransaction()
      .replace(R.id.idFrameLayout, fragment)
      .addToBackStack(null)
      .commit()
    return true
  }


//  override fun onPreferenceStartFragment(
//    caller: PreferenceFragmentCompat,
//    pref: Preference
//  ): Boolean {
//    val args = pref.extras
//    //val fragment = AboutTextFragment()
//    val fragment = supportFragmentManager.fragmentFactory.instantiate(
//      classLoader,
//      SettingsFragment::javaClass.toString()
//    )
//
//    fragment.arguments = args
//    fragment.setTargetFragment(caller, 0)
//
//    val myPref = pref.key
//
//    if (myPref == "dialog_preference") {
//      supportFragmentManager.beginTransaction()
//        .replace(R.id.idFrameLayout, fragment)
//        .addToBackStack(null)
//        .commit()
//    }
//    return true
//  }
}
