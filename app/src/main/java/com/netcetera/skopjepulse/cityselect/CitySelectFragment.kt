package com.netcetera.skopjepulse.cityselect

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.App
import com.netcetera.skopjepulse.base.BaseFragment
import com.netcetera.skopjepulse.countryCitySelector.CountryCitySelectorActivity
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.utils.ui.SwipeHelper
import kotlinx.android.synthetic.main.city_select_fragment_layout.*
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

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.city_select_fragment_layout, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    super.onViewCreated(view, savedInstanceState)


    citySelectRecyclerView.layoutManager = LinearLayoutManager(context)
    (citySelectRecyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

    citySelectAdapter = CitySelectAdapter()
    citySelectRecyclerView.adapter = citySelectAdapter
    citySelectAdapter.onCitySelected {
      mainViewModel.showForCity(it)
      parentFragmentManager.popBackStack()
    }

    addNewCityLinearLayout = linearLayoutAddNewCity
    linearLayoutAddNewCity.setOnClickListener {
      val intent = Intent(activity, CountryCitySelectorActivity::class.java)
      startActivity(intent)
    }


    citySelectRefreshView.setOnRefreshListener {
      viewModel.refreshData(true)
    }
    viewModel.showLoading.observe(viewLifecycleOwner, Observer {
      citySelectRefreshView.isRefreshing = it == true
    })

    //viewModel.shouldRefreshSelectedCities.observe(viewLifecycleOwner, Observer {
    viewModel.citySelectItems.observe(viewLifecycleOwner, citySelectAdapter)
    //})

    viewModel.requestLocationPermission.observe(viewLifecycleOwner, Observer { event ->
      event?.getContentIfNotHandled()?.let {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 24)
      }
    })

    /* Observe on what Measurement Type to show */
    mainViewModel.activeMeasurementType.observe(viewLifecycleOwner, Observer {
      viewModel.showDataForMeasurementType(it)
    })

    citySelectRecyclerView.addItemDecoration(DividerItemDecoration(citySelectRecyclerView.context, DividerItemDecoration.VERTICAL))

    val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(citySelectRecyclerView) {
      override fun instantiateUnderlayButton(position: Int): List<SwipeHelper.UnderlayButton> {
        var buttons = listOf<SwipeHelper.UnderlayButton>()
        val deleteButton = deleteButton(position)
        buttons = listOf(deleteButton)
        return buttons
      }
    })

    itemTouchHelper.attachToRecyclerView(citySelectRecyclerView)
  }

  private fun deleteButton(position: Int) : SwipeHelper.UnderlayButton {
    return SwipeHelper.UnderlayButton(requireContext(), "Delete", 14.0f, R.color.red_delete_button,
      object : SwipeHelper.UnderlayButtonClickListener {
        override fun onClick() {
          val cityToRemoveFromSharedPreferences = citySelectAdapter.del(position)
          viewModel.deleteCityOnSwipe(cityToRemoveFromSharedPreferences)
          Toast.makeText(activity, resources.getString(R.string.removed_message), Toast.LENGTH_SHORT).show()
        }
      })
  }

  override fun onResume() {
    super.onResume()
    Log.d("RESUME", "aaa")
    viewModel.getSelectedCities()
    viewModel.citySelectItems.observe(this,  { lista->
      Log.d("LISTA", lista.toString())

    })
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