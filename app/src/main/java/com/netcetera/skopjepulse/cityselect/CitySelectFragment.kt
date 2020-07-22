package com.netcetera.skopjepulse.cityselect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.netcetera.skopjepulse.countryCitySelector.CountryCitySelectorActivity
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.BaseFragment
import com.netcetera.skopjepulse.main.MainViewModel
import kotlinx.android.synthetic.main.city_select_fragment_layout.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Implementation of [CitySelectFragment] that is used for displaying of selected cities
 */
class CitySelectFragment : BaseFragment<CitySelectViewModel>(), CitySelectAdapter.OnCitySelectLongClickListener {
  override val viewModel: CitySelectViewModel by viewModel()
  private val mainViewModel : MainViewModel by sharedViewModel()
  private lateinit var citySelectAdapter : CitySelectAdapter

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.city_select_fragment_layout, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    citySelectRecyclerView.layoutManager = LinearLayoutManager(context)
    (citySelectRecyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

    citySelectAdapter = CitySelectAdapter(this)
    citySelectRecyclerView.adapter = citySelectAdapter
    citySelectAdapter.onCitySelected {
      mainViewModel.showForCity(it)
      parentFragmentManager.popBackStack()
    }

    addCity.setOnClickListener{
      val intent = Intent(activity, CountryCitySelectorActivity::class.java)
      startActivity(intent)
    }

    citySelectRefreshView.setOnRefreshListener {
      viewModel.refreshData(true)
    }
    viewModel.showLoading.observe(viewLifecycleOwner, Observer {
      citySelectRefreshView.isRefreshing = it == true
    })

    viewModel.requestLocationPermission.observe(viewLifecycleOwner, Observer { event ->
      event?.getContentIfNotHandled()?.let {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 24)
      }
    })

    /* Observe on what Measurement Type to show */
    mainViewModel.activeMeasurementType.observe(viewLifecycleOwner, Observer {
      viewModel.showDataForMeasurementType(it)
    })
  }

  override fun onResume() {
    super.onResume()
    viewModel.getSelectedCities()
    viewModel.citySelectItems.observe(viewLifecycleOwner, citySelectAdapter)
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
      grantResults: IntArray) {
    when(requestCode) {
      24 -> if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) viewModel.requestLocation()
      else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

  }

  override fun onCitySelectLongClick(city: CitySelectItem) {
   viewModel.deleteCity(city)
   viewModel.citySelectItems.observe(viewLifecycleOwner, citySelectAdapter)
  }
}