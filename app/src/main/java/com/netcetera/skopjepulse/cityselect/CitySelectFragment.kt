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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.netcetera.skopjepulse.countryCitySelector.CountryCitySelectorActivity
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.BaseFragment
import com.netcetera.skopjepulse.main.MainViewModel
import kotlinx.android.synthetic.main.city_select_fragment_layout.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class CitySelectFragment : BaseFragment<CitySelectViewModel>() {
  override val viewModel: CitySelectViewModel by viewModel()
  private val mainViewModel : MainViewModel by sharedViewModel()
  private lateinit var faButton: FloatingActionButton
  private lateinit var citySelectAdapter : CitySelectAdapter

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.city_select_fragment_layout, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    citySelectRecyclerView.layoutManager = LinearLayoutManager(context)
    (citySelectRecyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

    citySelectAdapter = CitySelectAdapter()
    citySelectRecyclerView.adapter = citySelectAdapter
    viewModel.citySelectItems.observe(viewLifecycleOwner, citySelectAdapter)
    citySelectAdapter.onCitySelected {
      mainViewModel.showForCity(it)
      parentFragmentManager.popBackStack()
    }

    faButton = fab
    faButton.setOnClickListener{
      val intent = Intent(activity, CountryCitySelectorActivity::class.java)
      startActivity(intent)
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        when (newState) {
          RecyclerView.SCROLL_STATE_IDLE -> fab.show()
          else -> fab.hide()
        }
        super.onScrollStateChanged(recyclerView, newState)
      }
    }
    citySelectRecyclerView.clearOnScrollListeners()
    citySelectRecyclerView.addOnScrollListener(scrollListener)

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
    viewModel.loadData()
    viewModel.citySelectItems.observe(viewLifecycleOwner, citySelectAdapter)
    citySelectAdapter.notifyDataSetChanged()
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
      grantResults: IntArray) {
    when(requestCode) {
      24 -> if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) viewModel.requestLocation()
      else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

  }
}