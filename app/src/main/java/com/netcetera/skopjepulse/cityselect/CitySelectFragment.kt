package com.netcetera.skopjepulse.cityselect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.BaseFragment
import com.netcetera.skopjepulse.base.model.City
import com.netcetera.skopjepulse.countryCitySelector.CityItem
import com.netcetera.skopjepulse.countryCitySelector.CountryCitySelectorActivity
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.map.MapFragment
import com.netcetera.skopjepulse.utils.ui.SwipeHelper
import kotlinx.android.synthetic.main.city_select_fragment_layout.*
import kotlinx.android.synthetic.main.city_select_item_layout.view.*
import kotlinx.android.synthetic.main.pulse_app_bar.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.HashSet

/**
 * Implementation of [CitySelectFragment] that is used for displaying of selected cities
 */
class CitySelectFragment : BaseFragment<CitySelectViewModel>() {
  override val viewModel: CitySelectViewModel by viewModel()
  private val mainViewModel: MainViewModel by sharedViewModel()
  private lateinit var historyCitySelectAdapter: HistoryCitySelectAdapter
  private lateinit var currentlyCitySelectAdapter: CurrentlyCitySelectAdapter
  private lateinit var addNewCityLinearLayout: LinearLayout

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

    historyCitySelectAdapter.onCitySelected {
      mainViewModel.showForCity(it)
      parentFragmentManager.popBackStack()
    }

    mainViewModel.activeCity.observe(viewLifecycleOwner, Observer { activeCity ->
      val citySelectedItems = viewModel.citySelectItems.value ?: listOf()
      val selectedList = citySelectedItems.filter { it.city.name == activeCity?.name }.toList()
      val historyList = citySelectedItems.filter { it.city.name != activeCity?.name }.toList()
      currentlySelected.value = selectedList
      history.value = historyList

      /*if (selectedList.isNotEmpty()) {
        currentlySelectedLinearLayout.visibility = View.VISIBLE
      } else {
        currentlySelectedLinearLayout.visibility = View.GONE
      }

      if (historyList.isNotEmpty()) {
        historyLinearLayout.visibility = View.VISIBLE
      } else {
        historyLinearLayout.visibility = View.GONE
      }*/
    })

    history.observe(viewLifecycleOwner, historyCitySelectAdapter)
    currentlySelected.observe(viewLifecycleOwner, currentlyCitySelectAdapter)

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

    /*viewModel.shouldRefreshSelectedCities.observe(viewLifecycleOwner, Observer {
      viewModel.citySelectItems.observe(viewLifecycleOwner, historyCitySelectAdapter)
    })*/

    viewModel.requestLocationPermission.observe(viewLifecycleOwner, Observer { event ->
      event?.getContentIfNotHandled()?.let {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 24)
      }
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
            resources.getString(R.string.removed_message),
            Toast.LENGTH_SHORT
          ).show()

          mainViewModel.activeCity.observe(viewLifecycleOwner, Observer { activeCity ->
            val citySelectedItems = viewModel.citySelectItems.value ?: listOf()
            val selectedList = citySelectedItems.filter { it.city.name == activeCity?.name }.toList()
            currentlySelected.value = selectedList

            if (selectedList.isNullOrEmpty()) {
              currentlySelectedLinearLayout.visibility = View.GONE
            }
          })
        }
      })

  }

  private fun currentlyDeleteButton(position: Int): SwipeHelper.UnderlayButton {
    return SwipeHelper.UnderlayButton(requireContext(), "Delete", 14.0f, R.color.red_delete_button,
      object : SwipeHelper.UnderlayButtonClickListener {
        override fun onClick() {
          val currentlyCityToRemoveFromSharedPreferences = currentlyCitySelectAdapter.del(position)
          viewModel.deleteCityOnSwipe(currentlyCityToRemoveFromSharedPreferences)
          Toast.makeText(
            activity,
            resources.getString(R.string.removed_message),
            Toast.LENGTH_SHORT
          ).show()


        /*mainViewModel.activeCity.observe(viewLifecycleOwner, Observer { activeCity ->
          val citySelectedItems = viewModel.citySelectItems.value ?: listOf()
          val selectedList = citySelectedItems.filter { it.city.name == activeCity?.name }.toList()
          currentlySelected.value = selectedList

          if (selectedList.isNullOrEmpty()) {
            currentlySelectedLinearLayout.visibility = View.GONE
          }
        })*/

        }
      })
  }

  override fun onResume() {
    super.onResume()
    viewModel.getSelectedCities()
    viewModel.citySelectItems.observe(viewLifecycleOwner, {
      val activeCity = mainViewModel.activeCity.value
      val citySelectedItems = it ?: listOf()
      val selectedList =
        citySelectedItems.filter { it2 -> it2.city.name == activeCity?.name }.toList()
      val historyList =
        citySelectedItems.filter { it2 -> it2.city.name != activeCity?.name }.toList()
      currentlySelected.value = selectedList
      history.value = historyList
    })
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