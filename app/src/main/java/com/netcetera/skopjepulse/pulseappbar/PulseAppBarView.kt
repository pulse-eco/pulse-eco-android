package com.netcetera.skopjepulse.pulseappbar

import android.view.View
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.model.City
import kotlinx.android.synthetic.main.pulse_app_bar.view.*
import java.util.*

class PulseAppBarView(private val pulseAppBarView: View) {

  private var selectedCityListener: (() -> Unit)? = null
  private var refreshRequestedListener: (() -> Unit)? = null

  init {
    pulseAppBarView.pulseAppbarLogo.setOnClickListener { refreshRequestedListener?.invoke() }
    pulseAppBarView.townLabel.setOnClickListener { selectedCityListener?.invoke() }
  }

  fun onCitySelectRequest(citySelectRequestListener: () -> Unit) {
    this.selectedCityListener = citySelectRequestListener
    pulseAppBarView.pulseCityPicker.apply {
      visibility = View.VISIBLE
    }
  }



  fun displayNoCityName() {
    val string = pulseAppBarView.context.getString(R.string.select_city)
    displayCityName(string)
  }

  fun displayCityName(city: City) {
    displayCityName(city.displayName.toUpperCase(Locale.getDefault()))
  }

  private fun displayCityName(name: String) {
    pulseAppBarView.townLabel.apply {
      text = name
      visibility = View.VISIBLE
    }

    pulseAppBarView.pulseCityPicker.apply {
      visibility = View.VISIBLE
    }
  }
}