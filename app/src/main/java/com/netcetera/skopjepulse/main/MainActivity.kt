package com.netcetera.skopjepulse.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.netcetera.skopjepulse.PulseLoadingIndicator
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.cityselect.CitySelectFragment
import com.netcetera.skopjepulse.dashboard.DashboardFragment
import com.netcetera.skopjepulse.map.MapFragment
import com.netcetera.skopjepulse.pulseappbar.PulseAppBarView
import com.netcetera.skopjepulse.settings.SettingsActivity
import com.netcetera.skopjepulse.utils.Internationalisation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.pulse_app_bar.*
import kotlinx.android.synthetic.main.simple_error_layout.errorView
import kotlinx.android.synthetic.main.view_picker_dialog.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension


class MainActivity : AppCompatActivity() {
  @OptIn(KoinApiExtension::class)
  private val mainViewModel: MainViewModel by viewModel()
  companion object {
    const val NEW_CITY_REQUEST_CODE = 12345
    const val NEW_CITY_NAME_RESULT = "cityName"
    const val SETTINGS_ACTIVITY_CODE = 666
    var SELECTED_FRAGMENT = ""
    lateinit var popupWindow: PopupWindow

    val dashFragment = DashboardFragment()
  }

  private val citySelectFragment: CitySelectFragment by lazy {
    CitySelectFragment()
  }

  private val appBarView: PulseAppBarView by lazy {
    PulseAppBarView(pulse_app_bar)
  }

  private val loadingIndicator: PulseLoadingIndicator by lazy {
    PulseLoadingIndicator(loadingIndicatorContainer)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Internationalisation.loadLocale(this)
    Internationalisation.loadLocale(applicationContext)
    setContentView(R.layout.activity_main)

    SELECTED_FRAGMENT = "map"

    btn_menu.setOnClickListener {
     val  pickerView =
        LayoutInflater.from(this).inflate(R.layout.view_picker_dialog, null) as ViewGroup

      popupWindow = PopupWindow(
        pickerView,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        true
      )

      if (popupWindow.isShowing) {
        popupWindow.dismiss()
      } else {
        popupWindow.showAsDropDown(it)
        if (SELECTED_FRAGMENT == "map") {
          popupWindow.contentView.mapView.isChecked = true
        } else if (SELECTED_FRAGMENT == "dashboard") {
          popupWindow.contentView.dashboardView.isChecked = true
        }
      }
    }

    mainViewModel.measurementTypeTabs.observe(this) {
      measurementTypeTabBarView.availableMeasurementTypes = it ?: emptyList()
    }
    measurementTypeTabBarView.selectedMeasurementType.observe(this) {
      mainViewModel.showForMeasurement(it)
    }

    appBarView.onCitySelectRequest {
      pulseCityPicker.setImageResource(R.drawable.ic_arrow_drop_up_24)
      val citySelectShown =
        supportFragmentManager.findFragmentById(R.id.content) is CitySelectFragment

      if (!citySelectShown) {
        supportFragmentManager
          .beginTransaction()
          .add(R.id.content, citySelectFragment)
          .addToBackStack(null)
          .commit()
      }
    }

    mainViewModel.activeCity.observe(this) { activeCity ->
      if (activeCity == null) {
        appBarView.displayNoCityName()
        showCitySelectIfNotShown()
      } else {
        appBarView.displayCityName(activeCity)
        pulseCityPicker.setImageResource(R.drawable.ic_arrow_drop_down_24)
        val existingMapFragment =
          supportFragmentManager.findFragmentByTag(activeCity.name) as? MapFragment
        if (existingMapFragment == null) {
           refreshMap()
        }
      }
    }
    mainViewModel.showLoading.observe(this, loadingIndicator)

    errorView?.let { errorTextView ->
      errorTextView.setOnClickListener {
        mainViewModel.refreshData(true)
      }
      mainViewModel.errorMessage.observe(this) {
        errorTextView.text = it
        if (it?.isNotBlank() == true) {
          errorTextView.visibility = View.VISIBLE
        } else {
          errorTextView.visibility = View.GONE
        }
      }
    }

    pulseAppbarLogo.setOnClickListener {
      if (SELECTED_FRAGMENT == "map"){
        refreshMap()
      }
    }

  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.view_menu, menu)
    return true
  }

  private fun showCitySelectIfNotShown() {
    val someFragmentShown = supportFragmentManager.findFragmentById(R.id.content) != null
    val citySelectShown =
      supportFragmentManager.findFragmentById(R.id.content) is CitySelectFragment
    if (!citySelectShown) {
      supportFragmentManager.beginTransaction().apply {
        add(R.id.content, citySelectFragment)
        if (someFragmentShown) addToBackStack(null)
      }.commit()
    }
  }

  private fun refreshMap() {
    val activeCity = mainViewModel.activeCity.value!!
    supportFragmentManager.beginTransaction().replace(
      R.id.content,
      MapFragment.newInstance(activeCity),
      activeCity.name,
    ).commit()
  }

  private fun refreshDashboard() {
    supportFragmentManager.beginTransaction().apply {
      replace(R.id.content, dashFragment)
      commit()
    }
  }

  override fun onResume() {
    super.onResume()
    mainViewModel.refreshData(false)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == NEW_CITY_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        if (data != null) {
          val result = data.getStringExtra(NEW_CITY_NAME_RESULT)
          if (result != null) {
            mainViewModel.showForCity(result)
          }
        }
      }
    }

    else if (requestCode == SETTINGS_ACTIVITY_CODE && resultCode == RESULT_OK) {}
    super.onActivityResult(requestCode, resultCode, data)
  }

  override fun onBackPressed() {
    super.onBackPressed()
    pulseCityPicker.setImageResource(R.drawable.ic_arrow_drop_down_24)
  }

  fun onRadioButtonClicked(view: View) {
    if (view is RadioButton && view.isChecked) {
      when (view.id) {
        R.id.dashboardView -> {
          supportFragmentManager.beginTransaction().apply {
            replace(R.id.content, dashFragment)
            commit()
          }
//          val activeCity = mainViewModel.activeCity.value!!
//          supportFragmentManager.beginTransaction().replace(
//            R.id.content,
//            DashboardFragment.newInstance(activeCity),
//            activeCity.name,
//          ).commit()
          setDismiss("dashboard")
//          val activeCity = mainViewModel.activeCity.value!!
//          supportFragmentManager.beginTransaction().replace(
//            R.id.content,
//            MapFragment.newInstance(activeCity),
//            activeCity.name,
//          ).commit()
        }
        R.id.mapView -> {
          val existingMapFragment =
            supportFragmentManager.findFragmentByTag(mainViewModel.activeCity.value!!.name) as? MapFragment
          if (existingMapFragment == null) {
            refreshMap()
          }
          setDismiss("map")
        }
        R.id.settingsView -> {
          val intent = Intent(this, SettingsActivity::class.java)
          startActivityForResult(intent, SETTINGS_ACTIVITY_CODE)
          setDismiss("")
        }
      }
    }
  }

  private fun setDismiss(text: String) {
    if (text.isNotEmpty()) SELECTED_FRAGMENT = text
    popupWindow.dismiss()
  }

}
