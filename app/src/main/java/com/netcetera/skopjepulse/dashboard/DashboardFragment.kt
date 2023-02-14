package com.netcetera.skopjepulse.dashboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.cityselect.CitySelectItem
import com.netcetera.skopjepulse.cityselect.CitySelectViewModel
import com.netcetera.skopjepulse.cityselect.CurrentlyCitySelectAdapter
import com.netcetera.skopjepulse.cityselect.HistoryCitySelectAdapter
import com.netcetera.skopjepulse.countryCitySelector.CountryCitySelectorActivity
import com.netcetera.skopjepulse.main.MainActivity
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.utils.ui.SwipeHelper
import kotlinx.android.synthetic.main.city_select_fragment_layout.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

class DashboardFragment : Fragment() {
  val viewModel: CitySelectViewModel by viewModel()
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
    return inflater.inflate(R.layout.fragment_dashboard, container, false)
  }

//
//  @OptIn(KoinApiExtension::class)
//  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//    super.onViewCreated(view, savedInstanceState)
//
//    currentlySelectedCityView = currentlySelectedLinearLayout
//
//    currentlyCityRecyclerView.layoutManager = LinearLayoutManager(context)
//    (currentlyCityRecyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations =
//      false
//
//    currentlyCitySelectAdapter = CurrentlyCitySelectAdapter()
//    currentlyCityRecyclerView.adapter = currentlyCitySelectAdapter
//
//    currentlyCitySelectAdapter.onCitySelected {
//      mainViewModel.showForCity(it)
//      parentFragmentManager.popBackStack()
//    }
//
//    mainViewModel.activeCity.observe(viewLifecycleOwner) {
//      handleCityLists(viewModel.citySelectItems.value)
//    }
//
//    currentlySelected.observe(viewLifecycleOwner, currentlyCitySelectAdapter)
//
////    historySelectRecyclerView.addItemDecoration(
////      DividerItemDecoration(
////        historySelectRecyclerView.context,
////        DividerItemDecoration.VERTICAL
////      )
////    )
////    currentlyCityRecyclerView.addItemDecoration(
////      DividerItemDecoration(
////        currentlyCityRecyclerView.context,
////        DividerItemDecoration.VERTICAL
////      )
////    )
//
//
//    val currentlyItemTouchHelper =
//      ItemTouchHelper(object : SwipeHelper(currentlyCityRecyclerView) {
//        override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
//          val buttons: List<UnderlayButton>
//          val deleteButton = currentlyDeleteButton(position)
//          buttons = listOf(deleteButton)
//          return buttons
//        }
//      })
//
//    currentlyItemTouchHelper.attachToRecyclerView(currentlyCityRecyclerView)
//  }
//
//
//  private fun currentlyDeleteButton(position: Int): SwipeHelper.UnderlayButton {
//    return SwipeHelper.UnderlayButton(requireContext(), "Delete", 14.0f, R.color.red_delete_button,
//      object : SwipeHelper.UnderlayButtonClickListener {
//        override fun onClick() {
//          val currentlyCityToRemoveFromSharedPreferences = currentlyCitySelectAdapter.del(position)
//          viewModel.deleteCityOnSwipe(currentlyCityToRemoveFromSharedPreferences)
//          mainViewModel.showForCity(null)
//          Toast.makeText(
//            activity,
//            resources.getString(R.string.msg_removed),
//            Toast.LENGTH_SHORT
//          ).show()
//        }
//      })
//  }
//
//  override fun onResume() {
//    super.onResume()
//    viewModel.getSelectedCities()
//  }
//
//  private fun handleCityLists(allSelectedCities: List<CitySelectItem>?) {
//    val activeCity = mainViewModel.activeCity.value
//    val citySelectedItems = allSelectedCities ?: listOf()
//    val selectedList =
//      citySelectedItems.filter { it2 -> it2.city.name == activeCity?.name }.toList()
//    val historyList =
//      citySelectedItems.filter { it2 -> it2.city.name != activeCity?.name }.toList()
//    currentlySelected.value = selectedList
//    history.value = historyList
//    currentlySelectedCityView.visibility = if (selectedList.isEmpty()) View.GONE else View.VISIBLE
//    historyCitiesView.visibility = if (historyList.isEmpty()) View.GONE else View.VISIBLE
//  }


}