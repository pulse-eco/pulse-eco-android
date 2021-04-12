package com.netcetera.skopjepulse.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.netcetera.skopjepulse.PulseLoadingIndicator
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.cityselect.CitySelectFragment
import com.netcetera.skopjepulse.databinding.ActivityMainBinding
import com.netcetera.skopjepulse.map.MapFragment
import com.netcetera.skopjepulse.pulseappbar.PulseAppBarView
import com.squareup.leakcanary.RefWatcher
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
  private val refWatcher : RefWatcher by inject()
  private val mainViewModel: MainViewModel by viewModel()
  private lateinit var views: ActivityMainBinding

  private val citySelectFragment: CitySelectFragment by lazy {
    CitySelectFragment()
  }

  private val appBarView : PulseAppBarView by lazy {
    PulseAppBarView(views.pulseAppBar.pulseAppBarCardView)
  }

  private val loadingIndicator: PulseLoadingIndicator by lazy {
    PulseLoadingIndicator(views.loadingIndicatorContainer)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    views = DataBindingUtil.setContentView(this,R.layout.activity_main)
    views.apply {
      viewModel = mainViewModel
      lifecycleOwner = this@MainActivity
      onErrorViewClickListener = onErrorClickListener
    }

    mainViewModel.measurementTypeTabs.observe(this, Observer {
      views.pulseAppBar.measurementTypeTabBarView.availableMeasurementTypes = it?: emptyList()
    })
    views.pulseAppBar.measurementTypeTabBarView.selectedMeasurementType.observe(this, Observer {
      mainViewModel.showForMeasurement(it)
    })

    appBarView.onCitySelectRequest {
      val citySelectShown = supportFragmentManager.findFragmentById(R.id.content) is CitySelectFragment
      if (!citySelectShown) {
        supportFragmentManager.beginTransaction()
          .add(R.id.content, citySelectFragment)
          .addToBackStack(null).commit()
      }
    }

    mainViewModel.activeCity.observe(this, Observer { activeCity ->
      if (activeCity == null) {
        showCitySelectIfNotShown()
      } else {
        appBarView.displayCityName(activeCity)
        val existingMapFragment = supportFragmentManager.findFragmentByTag(activeCity.name) as? MapFragment
        supportFragmentManager.beginTransaction()
          .replace(R.id.content, existingMapFragment ?: MapFragment.newInstance(activeCity), activeCity.name)
          .commit()
      }
    })

    mainViewModel.showLoading.observe(this, loadingIndicator)

  }

  private val onErrorClickListener = View.OnClickListener { mainViewModel.refreshData(true) }

  private fun showCitySelectIfNotShown() {
    val someFragmentShown = supportFragmentManager.findFragmentById(R.id.content) != null
    val citySelectShown = supportFragmentManager.findFragmentById(R.id.content) is CitySelectFragment
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
}
