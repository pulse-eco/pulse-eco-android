package com.netcetera.skopjepulse.settings
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import androidx.preference.PreferenceFragmentCompat
//import androidx.preference.Preference
//import androidx.preference.PreferenceFragmentCompat
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.dashboard.DashboardFragment
import com.netcetera.skopjepulse.main.MainActivity


class SettingsFragment : PreferenceFragment() {


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addPreferencesFromResource(R.xml.preferences)


//    val myPref = findPreference("dialog_preference")
//    myPref?.setOnPreferenceClickListener {
//      val fragment = AboutTextFragment()
//      val ft = fragmentManager?.beginTransaction()
//      ft?.replace(R.id.idFrameLayout,fragment)
//      ft?.addToBackStack(null)
//      ft?.commit()
//      true
//    }

//    val myPref = findPreference("dialog_preference")
//    myPref?.setOnPreferenceClickListener {
//        val intent = Intent(activity,AboutTextActivity::class.java)
//      startActivity(intent)
//      true
//    }

  }

//  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//    TODO("Not yet implemented")
//  }

}

