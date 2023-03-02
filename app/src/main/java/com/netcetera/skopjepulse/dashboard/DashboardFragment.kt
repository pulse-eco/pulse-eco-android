package com.netcetera.skopjepulse.dashboard

import android.graphics.Color
import android.icu.util.ULocale.getLanguage
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.BaseFragment
import com.netcetera.skopjepulse.base.data.DataDefinitionProvider
import com.netcetera.skopjepulse.base.model.*
import com.netcetera.skopjepulse.cityselect.CitySelectItem
import com.netcetera.skopjepulse.extensions.dimOnExpand
import com.netcetera.skopjepulse.extensions.onExpanded
import com.netcetera.skopjepulse.extensions.onStateChange
import com.netcetera.skopjepulse.historyforecast.calendar.CalendarValuesDataModel
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.map.GraphView
import com.netcetera.skopjepulse.map.MapFragment
import com.netcetera.skopjepulse.map.MapViewModel
import com.netcetera.skopjepulse.map.model.AverageWeeklyDataModel
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.bottom_sheet_content_layout.*
import kotlinx.android.synthetic.main.city_select_item_layout.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.map_fragment_layout.*
import kotlinx.android.synthetic.main.weekly_average.*
import kotlinx.android.synthetic.main.weekly_dashboard_average.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
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
    lateinit var citySelectItem: CitySelectItem
    lateinit var sensorType: MeasurementType
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


    viewModel.dataDefinitionDataPublicHelper.observe(viewLifecycleOwner) {
      dataDef = it
    }


    //value
    currentCityMeasurements = mainViewModel._currentCityMeasurements.value?.data!!
//    setMeasurementValue()

    mainViewModel.activeMeasurementType.observe(viewLifecycleOwner) { newMeasurement ->
      viewModel.showDataForMeasurementType(newMeasurement)
      activeMeasurement = newMeasurement
      citySelectMeasureLabel.text = dataDef.unit
       dataDef.bands.forEach {
         citySelectOverallStatus.text = it.shortGrade
       }
      setMeasurementValue()
      setDaysNames()
    }

    mainViewModel.activeCity.observe(viewLifecycleOwner) { city ->
      citySelectCityLabel.text = city?.name?.capitalize(Locale.ROOT)
      citySelectCountryLabel.text = city?.countryName?.capitalize(Locale.ROOT)
    }


    //SECOND ELEMENT

//    mainViewModel.activeMeasurementType.observe(viewLifecycleOwner) {
//
//      sensorType = it
//
//
//      viewModel.averageWeeklyData.value?.let { weeklyAverageDataModel ->
//        setSlidingPanelDailyDataValues(weeklyAverageDataModel)
//      }
//    }
  }

  private fun setMeasurementValue() {
    currentCityMeasurements[0].values.forEach { measurement ->
      if (activeMeasurement.equals(measurement.key)) {
        citySelectMeasureValue.text = measurement.value
        if(this::dataDef.isInitialized)
        {
          val band = getBand(measurement.value.toInt())
          citySelectMeasureContainer.setCardBackgroundColor(band!!.legendColor)
        }
      }
    }
  }


  private fun getBand(intValue: Int): Band? {
    return dataDef.findBandByValue(intValue)
  }

  private fun setSlidingPanelDailyDataValues(dataModel: AverageWeeklyDataModel?) {
    val listWeeklyAverageValues = getBoundValues()
    dataModel?.data?.let { readings ->
      readings.forEach { sensorReading ->
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat(
          Constants.MONTH_DAY_YEAR_DATE_FORMAT,
          Locale(getLanguage(context.toString()))
        )
        val dateOfSensorToString = format.format(sensorReading.stamp)
        cal.add(Calendar.DATE, -7)
        for (i in 0..6) {
          val iteratingDate = format.format(cal.time)
          if (dateOfSensorToString == iteratingDate) {
            listWeeklyAverageValues[i].text = sensorReading.value.toInt().toString()
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
  private fun getBoundsDays(): List<TextView> {
    return listOf(
      yesterday,twoDaysAgo,threeDaysAgo,fourDaysAgo,fiveDaysAgo
    )
  }
  private fun getBoundValues(): List<TextView> {
    return listOf(
      valYesterday, valTwoDaysAgo, valThreeDaysAgo, valFourDaysAgo, valFiveDaysAgo
    )
  }





}
