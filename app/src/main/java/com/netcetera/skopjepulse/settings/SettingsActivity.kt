package com.netcetera.skopjepulse.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.utils.Internationalisation
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings_layout)

    Internationalisation.loadLocale(this)
    Internationalisation.loadLocale(applicationContext)

    val toolbar = findViewById<Toolbar>(R.id.settings_go_back)

    setSupportActionBar(toolbar)
    supportActionBar?.title = ""

    btn_back.setOnClickListener {
      onBackPressed()
    }

  }
}