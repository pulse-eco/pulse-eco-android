package com.netcetera.skopjepulse.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.utils.Internationalisation
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings_layout)
    Internationalisation.loadLocale(this)

    val value = intent.extras?.get("Ana")
    Toast.makeText(
      this,
      value.toString(),
      Toast.LENGTH_SHORT
    ).show()

    btn_back.setOnClickListener {

      val data = Intent()
      data.putExtra("Ana", "Nazad sme")
      setResult(RESULT_OK, data)
      finish()

      onBackPressed()
    }

  }
}