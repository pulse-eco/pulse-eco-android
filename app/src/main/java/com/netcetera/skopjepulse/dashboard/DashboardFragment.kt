package com.netcetera.skopjepulse.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.BaseFragment
import com.netcetera.skopjepulse.base.model.Band
import com.netcetera.skopjepulse.base.model.City
import com.netcetera.skopjepulse.base.model.CityOverall
import com.netcetera.skopjepulse.base.model.DataDefinition
import com.netcetera.skopjepulse.cityselect.CitySelectItem
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.map.GraphView
import com.netcetera.skopjepulse.map.MapViewModel
import kotlinx.android.synthetic.main.city_select_item_layout.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*


class DashboardFragment : BaseFragment<MapViewModel>() {

  override val viewModel: MapViewModel by viewModel { parametersOf(city) }
  private val mainViewModel: MainViewModel by sharedViewModel()
  val city: City by lazy { requireArguments().getParcelable("city")!! }

  companion object {
    fun newInstance(city: City?) = DashboardFragment().apply {
      arguments = bundleOf(
        "city" to city
      )
    }

    lateinit var currentCityMeasurements: List<CityOverall>
    var activeMeasurement: String? = null
    lateinit var citySelectItem: CitySelectItem
  }

  lateinit var dataDef: DataDefinition

  private val graphView: GraphView by lazy {
    GraphView(dashboardGraph)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_dashboard, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.dataDefinitionDataPublicHelper.value?.let {
      dataDef = it
    }

    viewModel.dataDefinitionDataPublicHelper.observe(viewLifecycleOwner) {
      dataDef = it
    }

    //value
    currentCityMeasurements = mainViewModel._currentCityMeasurements.value?.data!!
    setMeasurementValue()

    mainViewModel.activeMeasurementType.observe(viewLifecycleOwner) { newMeasurement ->
      activeMeasurement = newMeasurement
      setMeasurementValue()
    }

    mainViewModel.activeCity.observe(viewLifecycleOwner) { city ->
      citySelectCityLabel.text = city?.name?.capitalize(Locale.ROOT)
      citySelectCountryLabel.text = city?.countryName?.capitalize(Locale.ROOT)
    }

  }

  private fun setMeasurementValue() {
    currentCityMeasurements[0].values.forEach { measurement ->
      if (activeMeasurement.equals(measurement.key)) {
        citySelectMeasureValue.text = measurement.value
        if (::dataDef.isInitialized) {
          val band = getBand(measurement.value.toInt())
          citySelectMeasureContainer.setCardBackgroundColor(band.legendColor)
        }
      }
    }
  }

  private fun getBand(intValue: Int): Band {
    return dataDef.findBandByValue(intValue)
  }

}
