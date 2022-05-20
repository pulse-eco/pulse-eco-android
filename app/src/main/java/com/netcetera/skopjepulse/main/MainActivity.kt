package com.netcetera.skopjepulse.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.PulseLoadingIndicator
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.cityselect.CitySelectFragment
import com.netcetera.skopjepulse.historyAndForecast.HistoryAndForecastAdapter
import com.netcetera.skopjepulse.historyAndForecast.HistoryAndForecastDataModel
import com.netcetera.skopjepulse.map.MapFragment
import com.netcetera.skopjepulse.pulseappbar.PulseAppBarView
import com.netcetera.skopjepulse.showConformationDialog
import com.netcetera.skopjepulse.utils.Internationalisation
import com.squareup.leakcanary.RefWatcher
import kotlinx.android.synthetic.main.activity_main.loadingIndicatorContainer
import kotlinx.android.synthetic.main.activity_main.pulse_app_bar
import kotlinx.android.synthetic.main.history_and_forecast.*
import kotlinx.android.synthetic.main.language_picker_dilog.view.*
import kotlinx.android.synthetic.main.pulse_app_bar.*
import kotlinx.android.synthetic.main.simple_error_layout.errorView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
  private val refWatcher: RefWatcher by inject()
  private val mainViewModel: MainViewModel by viewModel()

  companion object {
    const val NEW_CITY_REQUEST_CODE = 12345
    const val NEW_CITY_NAME_RESULT = "cityName"
    const val HISTORY_FORECAST_LIST_SIZE = 7
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

    historyAndForecastRecyclerView.layoutManager =
      LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    val historyForecastAdapter = HistoryAndForecastAdapter(this, getButtonsList())
    historyAndForecastRecyclerView.adapter = historyForecastAdapter
    historyAndForecastRecyclerView.scrollToPosition(5)


    btn_language.setOnClickListener {
      val lang = getSharedPreferences(
        Constants.LANGUAGE_CODE,
        Context.MODE_PRIVATE
      ).getString(Constants.LANGUAGE_CODE, "")
      val pickerView =
        LayoutInflater.from(this).inflate(R.layout.language_picker_dilog, null) as ViewGroup

      pickerView.mapTypeRadioGroup.check(
        when (lang) {
          "mk" -> R.id.language_mk
          "en" -> R.id.language_en
          "de" -> R.id.language_de
          "ro" -> R.id.language_ro
          else -> 0
        }
      )

      val popupWindow = PopupWindow(
        pickerView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
        true
      )

      pickerView.mapTypeRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
        when (i) {
          R.id.language_en -> {
            popupWindow.dismiss()
            showConformationDialog(
              this,
              getString(R.string.change_language_message_android)
            ) { changeLanguage("en") }
          }
          R.id.language_mk -> {
            popupWindow.dismiss()
            showConformationDialog(
              this,
              getString(R.string.change_language_message_android)
            ) { changeLanguage("mk") }
          }
          R.id.language_de -> {
            popupWindow.dismiss()
            showConformationDialog(
              this,
              getString(R.string.change_language_message_android)
            ) { changeLanguage("de") }
          }
          R.id.language_ro -> {
            popupWindow.dismiss()
            showConformationDialog(
              this,
              getString(R.string.change_language_message_android)
            ) { changeLanguage("ro") }
          }
        }
      }

      if (popupWindow.isShowing)
        popupWindow.dismiss()
      else
        if (!popupWindow.isShowing) popupWindow.showAsDropDown(it)
    }

    mainViewModel.measurementTypeTabs.observe(this, Observer {
      measurementTypeTabBarView.availableMeasurementTypes = it ?: emptyList()
    })
    measurementTypeTabBarView.selectedMeasurementType.observe(this, Observer {
      mainViewModel.showForMeasurement(it)
    })

    appBarView.onCitySelectRequest {
      pulseAppbarDropDown.visibility = View.GONE
      pulseAppbarDropUp.visibility = View.VISIBLE
      val citySelectShown =
        supportFragmentManager.findFragmentById(R.id.content) is CitySelectFragment
      if (!citySelectShown) {
        supportFragmentManager.beginTransaction()
          .add(R.id.content, citySelectFragment)
          .addToBackStack(null).commit()
      }
    }

    mainViewModel.activeCity.observe(this, Observer { activeCity ->
      if (activeCity == null) {
        appBarView.displayNoCityName()
        showCitySelectIfNotShown()
      } else {
        appBarView.displayCityName(activeCity)
        val existingMapFragment =
          supportFragmentManager.findFragmentByTag(activeCity.name) as? MapFragment
        if (existingMapFragment == null) {
          supportFragmentManager.beginTransaction()
            .replace(
              R.id.content,
              MapFragment.newInstance(activeCity),
              activeCity.name
            )
            .commit()
        }
      }
    })

    mainViewModel.showLoading.observe(this, loadingIndicator)

    errorView?.let { errorTextView ->
      errorTextView.setOnClickListener {
        mainViewModel.refreshData(true)
      }
      mainViewModel.errorMessage.observe(this, Observer {
        errorTextView.text = it
        if (it?.isNotBlank() == true) {
          errorTextView.visibility = View.VISIBLE
        } else {
          errorTextView.visibility = View.GONE
        }
      })
    }
  }


  private fun getButtonsList(): ArrayList<HistoryAndForecastDataModel> {
    val list = ArrayList<HistoryAndForecastDataModel>()
    list.add(HistoryAndForecastDataModel("Item 1", HistoryAndForecastAdapter.VIEW_TYPE_EXPLORE))
    for (i in 1..HISTORY_FORECAST_LIST_SIZE) {
      list.add(HistoryAndForecastDataModel("Item 2", HistoryAndForecastAdapter.VIEW_TYPE_DATE))
    }
    return list
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

  override fun onResume() {
    super.onResume()
    mainViewModel.refreshData(false)
  }

  override fun onDestroy() {
    super.onDestroy()
    refWatcher.watch(this)
  }

  private fun changeLanguage(localeName: String) {
    Internationalisation.setLocale(this, localeName)
    Internationalisation.setLocale(applicationContext, localeName)
    mainViewModel.reloadDDPData()

    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == NEW_CITY_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        if (data != null) {
          val result = data.getStringExtra(NEW_CITY_NAME_RESULT)
          if (result != null) {
            mainViewModel.showForCity(result)

          }
        }
      }
    }
    super.onActivityResult(requestCode, resultCode, data)
  }
}