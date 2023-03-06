package com.netcetera.skopjepulse.dashboard

import android.icu.util.ULocale.getLanguage
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.BaseFragment
import com.netcetera.skopjepulse.base.model.*
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.map.GraphView
import com.netcetera.skopjepulse.map.MapViewModel
import com.netcetera.skopjepulse.map.model.AverageWeeklyDataModel
import com.netcetera.skopjepulse.map.model.GraphModel
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.single_city_layout.*
import kotlinx.android.synthetic.main.weekly_dashboard_average.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
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
  }

  lateinit var dataDef: DataDefinition
  lateinit var setVal : AverageWeeklyDataModel


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

  @OptIn(KoinApiExtension::class)
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.dataDefinitionDataPublicHelper.observe(viewLifecycleOwner) {
      dataDef = it
    }


    //value
    currentCityMeasurements = mainViewModel._currentCityMeasurements.value?.data!!
//    setMeasurementValue()

    mainViewModel.activeMeasurementType.observe(viewLifecycleOwner) { newMeasurement ->
      viewModel.showDataForMeasurementType(newMeasurement)
      activeMeasurement = newMeasurement
      setValuesForMeasurement()
      setDaysNames()
      displayUnit()

      viewModel.averageWeeklyData.value.let { averageWeeklyDataModel ->
        setSlidingPanelDailyDataValues(averageWeeklyDataModel)
      }
      viewModel.averageWeeklyData.observe(viewLifecycleOwner){
        setVal = it
        setSlidingPanelDailyDataValues(setVal)
      }
    }

    mainViewModel.activeCity.observe(viewLifecycleOwner) { city ->
      setLocationInfo()
    }


  }

  private fun setLocationInfo() {
    textViewCityName.text = city.name.capitalize(Locale.ROOT)
    textViewCountryName.text = city.countryName.capitalize(Locale.ROOT)
  }
  private fun setValuesForMeasurement() {
    currentCityMeasurements[0].values.forEach { measurement ->
      if (activeMeasurement.equals(measurement.key)) {
        textViewMeasurementValue.text = measurement.value
        if(this::dataDef.isInitialized) {
          val band = getBand(measurement.value.toInt())
          cardViewMeasurementColor.setCardBackgroundColor(band.legendColor)

//          getBoundValues().forEach {
//            it.setTextColor(band.legendColor)}
//          getBoundUnits().forEach {
//            it.setTextColor(band.legendColor)
//          }


          textViewShortGrade.text = band.shortGrade
          textViewMeasurementUnit.text = dataDef.unit
        }
      }
    }
  }

  private fun getBand(intValue: Int): Band {
    return dataDef.findBandByValue(intValue)
  }

  private fun setSlidingPanelDailyDataValues(dataModel: AverageWeeklyDataModel?) {
    val listWeeklyAverageValues = getBoundValues()
    val listWeeklyAverageUnit = getBoundUnits()

    dataModel?.data?.let { readings ->
      readings.forEach { sensorReading ->
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat(
          Constants.MONTH_DAY_YEAR_DATE_FORMAT,
          Locale(getLanguage(context.toString()))
        )
        val band = getBand(sensorReading.value.toInt())
//        cardViewMeasurementColor.setCardBackgroundColor(band.legendColor)
        val dateOfSensorToString = format.format(sensorReading.stamp)
        cal.add(Calendar.DATE, -5)
        for (i in 0..4) {
          val iteratingDate = format.format(cal.time)
          if (dateOfSensorToString == iteratingDate) {
            listWeeklyAverageValues[i].setTextColor(band.legendColor)
            listWeeklyAverageValues[i].text = sensorReading.value.toInt().toString()
            listWeeklyAverageUnit[i].setTextColor(band.legendColor)
          }
          cal.add(Calendar.DATE, 1)
        }
      }
    }
  }

  private fun setDaysNames() {
    val cal = Calendar.getInstance()
    val listOfDaysNames = getBoundsDays()
    val formatter = SimpleDateFormat("EEE", Locale(getLanguage(context.toString())))
    listOfDaysNames.forEach {
      cal.add(Calendar.DATE, -1)
      it.text = formatter.format(cal.time)
    }
  }

  private fun displayUnit() {
    val listOfUnits = getBoundUnits()
    listOfUnits.forEach {
      it.text = dataDef.unit
    }
  }

  private fun getBoundsDays(): List<TextView> {
    return listOf(
      tvOneDayAgoName, tvTwoDaysAgoName, tvThreeDaysAgoName, tvFourDaysAgoName, tvFiveDaysAgoName
    )
  }

  private fun getBoundValues(): List<TextView> {
    return listOf(
      tvFiveDaysAgoValue,
      tvFourDaysAgoValue,
      tvThreeDaysAgoValue,
      tvTwoDaysAgoValue,
      tvOneDayAgoValue
    )
  }

  private fun getBoundUnits(): List<TextView> {
    return listOf(
      tvFiveDaysAgoUnit,
      tvFourDaysAgoUnit,
      tvThreeDaysAgoUnit,
      tvTwoDaysAgoUnit,
      tvOneDayAgoValueUnit
    )
  }

}

