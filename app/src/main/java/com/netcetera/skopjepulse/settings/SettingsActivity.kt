package com.netcetera.skopjepulse.settings
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.utils.Internationalisation
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings_layout)
    Internationalisation.loadLocale(this)


    btn_back.setOnClickListener {
      finish()
      onBackPressed()
    }
  }

}
