package com.netcetera.skopjepulse.cityselect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.BaseFragment
import com.netcetera.skopjepulse.countryCitySelector.CountryCitySelectorActivity
import com.netcetera.skopjepulse.databinding.CitySelectFragmentLayoutBinding
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.utils.ui.SwipeHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Implementation of [CitySelectFragment] that is used for displaying of selected cities
 */
class CitySelectFragment : BaseFragment<CitySelectViewModel>() {
  override val viewModel: CitySelectViewModel by viewModel()
  private val mainViewModel : MainViewModel by sharedViewModel()
  private lateinit var citySelectAdapter : CitySelectAdapter
  private lateinit var addNewCityLinearLayout: LinearLayout
  private lateinit var views: CitySelectFragmentLayoutBinding

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    views = CitySelectFragmentLayoutBinding.inflate(inflater,container,false)
    return views.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    views.citySelectRecyclerView.layoutManager = LinearLayoutManager(context)
    (views.citySelectRecyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

    citySelectAdapter = CitySelectAdapter()
    views.citySelectRecyclerView.adapter = citySelectAdapter
    citySelectAdapter.onCitySelected {
      mainViewModel.showForCity(it)
      parentFragmentManager.popBackStack()
    }

    addNewCityLinearLayout = views.linearLayoutAddNewCity
    views.linearLayoutAddNewCity.setOnClickListener {
      val intent = Intent(activity, CountryCitySelectorActivity::class.java)
      startActivity(intent)
    }


    views.citySelectRefreshView.setOnRefreshListener {
      viewModel.refreshData(true)
    }
    viewModel.showLoading.observe(viewLifecycleOwner, Observer {
      views.citySelectRefreshView.isRefreshing = it == true
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

    views.citySelectRecyclerView.addItemDecoration(DividerItemDecoration(views.citySelectRecyclerView.context, DividerItemDecoration.VERTICAL))

    val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(views.citySelectRecyclerView) {
      override fun instantiateUnderlayButton(position: Int): List<SwipeHelper.UnderlayButton> {
        var buttons = listOf<SwipeHelper.UnderlayButton>()
        val deleteButton = deleteButton(position)
        buttons = listOf(deleteButton)
        return buttons
      }
    })

    itemTouchHelper.attachToRecyclerView(views.citySelectRecyclerView)
  }

  private fun deleteButton(position: Int) : SwipeHelper.UnderlayButton {
    return SwipeHelper.UnderlayButton(requireContext(), "Delete", 14.0f, R.color.red_delete_button,
      object : SwipeHelper.UnderlayButtonClickListener {
        override fun onClick() {
          val cityToRemoveFromSharedPreferences = citySelectAdapter.del(position)
          viewModel.deleteCityOnSwipe(cityToRemoveFromSharedPreferences)
          Toast.makeText(activity, "Removed from selected cities", Toast.LENGTH_SHORT).show()
        }
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
}