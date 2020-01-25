package com.netcetera.skopjepulse.pulseappbar

import android.view.View
import com.netcetera.skopjepulse.base.model.City
import kotlinx.android.synthetic.main.pulse_app_bar.view.pulseAppbarCitySelect
import kotlinx.android.synthetic.main.pulse_app_bar.view.pulseAppbarLogo
import kotlinx.android.synthetic.main.pulse_app_bar.view.townLabel

class PulseAppBarView(private val pulseAppBarView: View) {

  private var selectedCityListener : (() -> Unit)? = null
  private var refreshRequestedListener : (() -> Unit)? = null

  init {
    pulseAppBarView.pulseAppbarLogo.setOnClickListener { refreshRequestedListener?.invoke() }
    pulseAppBarView.pulseAppbarCitySelect.setOnClickListener { selectedCityListener?.invoke() }
  }

  fun onCitySelectRequest(citySelectRequestListener: () -> Unit) {
    this.selectedCityListener = citySelectRequestListener
  }

  fun onRefreshRequested(func: () -> Unit) {
    this.refreshRequestedListener = func
  }

  fun displayCityName(city: City) {
    pulseAppBarView.townLabel.apply {
      text = city.name.toUpperCase()
      visibility = View.VISIBLE
    }
  }
}