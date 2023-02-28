package com.netcetera.skopjepulse.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.BaseFragment
import com.netcetera.skopjepulse.base.model.Band
import com.netcetera.skopjepulse.base.model.City
import com.netcetera.skopjepulse.base.model.CityOverall
import com.netcetera.skopjepulse.base.model.DataDefinition
import com.netcetera.skopjepulse.cityselect.CitySelectItem
import com.netcetera.skopjepulse.cityselect.CitySelectViewModel
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.map.GraphView
import com.netcetera.skopjepulse.map.MapViewModel
import com.netcetera.skopjepulse.map.model.SensorOverviewModel
import com.netcetera.skopjepulse.pulseappbar.MeasurementTypeTab
import kotlinx.android.synthetic.main.city_select_item_layout.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.view_picker_dialog.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*


class DashboardFragment : BaseFragment<MapViewModel>() {

//  override val viewModel: CitySelectViewModel by viewModel()
  override val viewModel: MapViewModel by viewModel()
  private val mainViewModel: MainViewModel by sharedViewModel()

  private val graphView: GraphView by lazy {
    GraphView(dashboardGraph)
  }

  companion object {

    var activeMeasurement: String? = null
    lateinit var citySelectItem: CitySelectItem
    lateinit var currentCityMeasurements: List<CityOverall>
    lateinit var dataDef: DataDefinition
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
//    activeMeasurement = mainViewModel.activeMeasurementType.value

    activeMeasurement = mainViewModel.activeMeasurementType.value.toString()

    //CITY AND COUNTRY NAME
    mainViewModel.activeCity.observe(viewLifecycleOwner) { city ->
      citySelectItem?.city = city!!
      citySelectCityLabel.text = city?.name.capitalize(Locale.ROOT)
      citySelectCountryLabel.text = city?.countryName?.capitalize(Locale.ROOT)
      val band = getBand(activeMeasurement!!.toInt())
      citySelectMeasureContainer.setCardBackgroundColor(band.legendColor)
      citySelectMeasureLabel.text = band.shortGrade

      citySelectItem = CitySelectItem(
        city,
        band.shortGrade,
        activeMeasurement.toString(),
        dataDef.unit,
        band.legendColor
      )

    }

    //value
    currentCityMeasurements = mainViewModel._currentCityMeasurements.value?.data!!
    iterate()
    mainViewModel.activeMeasurementType.observe(viewLifecycleOwner) { newMeasurement ->
      activeMeasurement = newMeasurement
      iterate()
    }

  }

//  val measurementBand = dataDefinition.findBandByValue(measurement.toInt())
//  CitySelectItem(
//  city,
//  measurementBand.shortGrade,
//  measurement.toInt().toString(),
//  dataDefinition.unit,
//  measurementBand.legendColor
//  )

  private fun getBand(intValue: Int): Band {
    return dataDef.findBandByValue(intValue)
  }

  private fun iterate() {
    currentCityMeasurements[0].values.forEach { measurement ->
      if (activeMeasurement.equals(measurement.key)) {
//        activeMeasurementValue = measurement.value
        citySelectMeasureValue.text = measurement.value
      }
    }
  }
}