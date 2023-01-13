package com.netcetera.skopjepulse.settings
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.utils.Internationalisation
import kotlinx.android.synthetic.main.activity_settings_toolbar.*

class SettingsActivity : AppCompatActivity() {

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

}
