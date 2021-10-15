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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.BaseFragment
import com.netcetera.skopjepulse.countryCitySelector.CountryCitySelectorActivity
import com.netcetera.skopjepulse.main.MainActivity.Companion.NEW_CITY_REQUEST_CODE
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
  private val mainViewModel: MainViewModel by sharedViewModel()
  private lateinit var historyCitySelectAdapter: HistoryCitySelectAdapter
  private lateinit var currentlyCitySelectAdapter: CurrentlyCitySelectAdapter
  private lateinit var addNewCityLinearLayout: LinearLayout
  private lateinit var currentlySelectedCityView: LinearLayout
  private lateinit var historyCitiesView: LinearLayout

  private var currentlySelected = MutableLiveData<List<CitySelectItem>>()
  private var history = MutableLiveData<List<CitySelectItem>>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.city_select_fragment_layout, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    currentlySelectedCityView = currentlySelectedLinearLayout
    historyCitiesView = historyLinearLayout

    historySelectRecyclerView.layoutManager = LinearLayoutManager(context)
    (historySelectRecyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations =
      false

    currentlyCityRecyclerView.layoutManager = LinearLayoutManager(context)
    (currentlyCityRecyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations =
      false

    historyCitySelectAdapter = HistoryCitySelectAdapter()
    historySelectRecyclerView.adapter = historyCitySelectAdapter

    currentlyCitySelectAdapter = CurrentlyCitySelectAdapter()
    currentlyCityRecyclerView.adapter = currentlyCitySelectAdapter

    currentlyCitySelectAdapter.onCitySelected {
      mainViewModel.showForCity(it)
      parentFragmentManager.popBackStack()
    }

    historyCitySelectAdapter.onCitySelected {
      mainViewModel.showForCity(it)
      parentFragmentManager.popBackStack()
    }

    mainViewModel.activeCity.observe(viewLifecycleOwner, Observer { activeCity ->
      handleCityLists(viewModel.citySelectItems.value)
    })

    history.observe(viewLifecycleOwner, historyCitySelectAdapter)
    currentlySelected.observe(viewLifecycleOwner, currentlyCitySelectAdapter)

    addNewCityLinearLayout = linearLayoutAddNewCity
    linearLayoutAddNewCity.setOnClickListener {
      val intent = Intent(activity, CountryCitySelectorActivity::class.java)
      activity?.startActivityForResult(intent, NEW_CITY_REQUEST_CODE)
    }

    citySelectRefreshView.setOnRefreshListener {
      viewModel.refreshData(true)
    }

    viewModel.showLoading.observe(viewLifecycleOwner, Observer {
      citySelectRefreshView.isRefreshing = it == true
    })

    /*viewModel.shouldRefreshSelectedCities.observe(viewLifecycleOwner, Observer {
      viewModel.citySelectItems.observe(viewLifecycleOwner, historyCitySelectAdapter)
    })*/

    viewModel.requestLocationPermission.observe(viewLifecycleOwner, Observer { event ->
      event?.getContentIfNotHandled()?.let {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 24)
      }
    })

    viewModel.citySelectItems.observe(viewLifecycleOwner, {
      handleCityLists(it)
    })

    /* Observe on what Measurement Type to show */
    mainViewModel.activeMeasurementType.observe(viewLifecycleOwner, Observer {
      viewModel.showDataForMeasurementType(it)
    })

    historySelectRecyclerView.addItemDecoration(
      DividerItemDecoration(
        historySelectRecyclerView.context,
        DividerItemDecoration.VERTICAL
      )
    )
    currentlyCityRecyclerView.addItemDecoration(
      DividerItemDecoration(
        currentlyCityRecyclerView.context,
        DividerItemDecoration.VERTICAL
      )
    )

    val historyItemTouchHelper = ItemTouchHelper(object : SwipeHelper(historySelectRecyclerView) {
      override fun instantiateUnderlayButton(position: Int): List<SwipeHelper.UnderlayButton> {
        var buttons = listOf<SwipeHelper.UnderlayButton>()
        val deleteButton = historyDeleteButton(position)
        buttons = listOf(deleteButton)
        return buttons
      }
    })

    val currentlyItemTouchHelper = ItemTouchHelper(object : SwipeHelper(currentlyCityRecyclerView) {
      override fun instantiateUnderlayButton(position: Int): List<SwipeHelper.UnderlayButton> {
        var buttons = listOf<SwipeHelper.UnderlayButton>()
        val deleteButton = currentlyDeleteButton(position)
        buttons = listOf(deleteButton)
        return buttons
      }
    })

    historyItemTouchHelper.attachToRecyclerView(historySelectRecyclerView)
    currentlyItemTouchHelper.attachToRecyclerView(currentlyCityRecyclerView)
  }

  private fun historyDeleteButton(position: Int): SwipeHelper.UnderlayButton {
    return SwipeHelper.UnderlayButton(requireContext(), "Delete", 14.0f, R.color.red_delete_button,
      object : SwipeHelper.UnderlayButtonClickListener {
        override fun onClick() {
          val historyCityToRemoveFromSharedPreferences = historyCitySelectAdapter.del(position)
          viewModel.deleteCityOnSwipe(historyCityToRemoveFromSharedPreferences)
          Toast.makeText(
            activity,
            resources.getString(R.string.msg_removed),
            Toast.LENGTH_SHORT
          ).show()
        }
      })

  }

  private fun currentlyDeleteButton(position: Int): SwipeHelper.UnderlayButton {
    return SwipeHelper.UnderlayButton(requireContext(), "Delete", 14.0f, R.color.red_delete_button,
      object : SwipeHelper.UnderlayButtonClickListener {
        override fun onClick() {
          val currentlyCityToRemoveFromSharedPreferences = currentlyCitySelectAdapter.del(position)
          viewModel.deleteCityOnSwipe(currentlyCityToRemoveFromSharedPreferences)
          mainViewModel.showForCity(null)
          Toast.makeText(
            activity,
            resources.getString(R.string.msg_removed),
            Toast.LENGTH_SHORT
          ).show()
        }
      })
  }

  override fun onResume() {
    super.onResume()
    viewModel.getSelectedCities()
  }

  private fun handleCityLists(allSelectedCities: List<CitySelectItem>?) {
    val activeCity = mainViewModel.activeCity.value
    val citySelectedItems = allSelectedCities ?: listOf()
    val selectedList =
      citySelectedItems.filter { it2 -> it2.city.name == activeCity?.name }.toList()
    val historyList =
      citySelectedItems.filter { it2 -> it2.city.name != activeCity?.name }.toList()
    currentlySelected.value = selectedList
    history.value = historyList
    currentlySelectedCityView.visibility = if (selectedList.isEmpty()) View.GONE else View.VISIBLE
    historyCitiesView.visibility = if (historyList.isEmpty()) View.GONE else View.VISIBLE
  }

  override fun onRequestPermissionsResult(
    requestCode: Int, permissions: Array<out String>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      24 -> if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) viewModel.requestLocation()
      else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

  }
}