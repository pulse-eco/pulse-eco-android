package com.netcetera.skopjepulse.map

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.google.android.gms.maps.model.Polygon
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.like.LikeButton
import com.like.OnLikeListener
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.PulseLoadingIndicator
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.BaseFragment
import com.netcetera.skopjepulse.base.data.Resource
import com.netcetera.skopjepulse.base.model.*
import com.netcetera.skopjepulse.extensions.*
import com.netcetera.skopjepulse.favouritesensors.showFavouriteSensorsPicker
import com.netcetera.skopjepulse.historyforecast.HistoryForecastAdapter
import com.netcetera.skopjepulse.historyforecast.HistoryForecastDataModel
import com.netcetera.skopjepulse.historyforecast.calendar.CalendarValuesDataModel
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.map.mapvisualization.MapMarkersController
import com.netcetera.skopjepulse.map.model.AverageWeeklyDataModel
import com.netcetera.skopjepulse.map.model.BottomSheetPeekViewModel
import com.netcetera.skopjepulse.map.model.GraphModel
import com.netcetera.skopjepulse.map.overallbanner.Legend
import com.netcetera.skopjepulse.map.overallbanner.OverallBannerView
import com.netcetera.skopjepulse.map.preferences.MapPreferencesPopup
import com.netcetera.skopjepulse.showDisclaimerDialog
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.bottom_sheet_content_layout.*
import kotlinx.android.synthetic.main.bottom_sheet_default_peek.*
import kotlinx.android.synthetic.main.bottom_sheet_no_sensors_layout.*
import kotlinx.android.synthetic.main.bottom_sheet_sensor_overview_peek.*
import kotlinx.android.synthetic.main.bottom_sheet_sensor_overview_peek.view.*
import kotlinx.android.synthetic.main.history_and_forecast.*
import kotlinx.android.synthetic.main.map_fragment_layout.*
import kotlinx.android.synthetic.main.map_loading_indicator.*
import kotlinx.android.synthetic.main.overall_banner_layout.*
import kotlinx.android.synthetic.main.overall_banner_layout.view.*
import kotlinx.android.synthetic.main.weekly_average.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat


import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class MapFragment : BaseFragment<MapViewModel>() {
  override val viewModel: MapViewModel by viewModel { parametersOf(city) }
  private val mainViewModel: MainViewModel by sharedViewModel()

  lateinit var dataDef: DataDefinition
  lateinit var sensorType: MeasurementType

  private var pastWeekDataLabelForCityName: Boolean = true
  lateinit var historyForecastAdapter: HistoryForecastAdapter

  val city: City by lazy { requireArguments().getParcelable("city")!! }


  private val loadingIndicator: PulseLoadingIndicator by lazy {
    PulseLoadingIndicator(loadingIndicatorContainer)
  }
  private val mapPreferencesPopup: MapPreferencesPopup by lazy {
    MapPreferencesPopup(requireContext())
  }
  private val graphView: GraphView by lazy {
    GraphView(sensorMeasurementsGraph)
  }
  private lateinit var mapMarkersController: MapMarkersController

  private val overallBanner by lazy {
    OverallBannerView(overallBannerView)
  }



  companion object {
    fun newInstance(city: City) = MapFragment().apply {
      arguments = bundleOf(
        "city" to city
      )
    }
    var overAllData: List<CityOverall>? = null
    var calendarValuesResult : List<CalendarValuesDataModel> = listOf()
    lateinit var SENSOR_TYPE: MeasurementType
    var bandValueOverallData: Int? = null
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.map_fragment_layout, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    map.onCreate(savedInstanceState)
    // Declarations and interactions
    mapMarkersController = MapMarkersController(map) {viewModel.selectSensor(it) }

    mainViewModel.overall(this.city.name).observe(viewLifecycleOwner, object : Observer<Resource<List<CityOverall>>> {
        override fun onChanged(t: Resource<List<CityOverall>>?) {
          overAllData = t?.data
        }
      })

      /* Observe on what Measurement Type to show */
    mainViewModel.activeMeasurementType.observe(viewLifecycleOwner) {
      viewModel.showDataForMeasurementType(it)
      sensorType = it
      SENSOR_TYPE = sensorType
      progressBarForWeeklyAverageData.visibility = View.VISIBLE
      weeklyAverageView.visibility = View.GONE
      setDaysNames()

      viewModel.overallBannerData.observe(viewLifecycleOwner, overallBanner)
      setMapValuesToday()


      viewModel.averageWeeklyData.value?.let { weeklyAverageDataModel ->
        setValueForAverageDailyData(weeklyAverageDataModel)
//        setValueForSevenDaysRange(weeklyAverageDataModel, overAllData?.last(), sensorType)
      }
      viewModel.averageDataGivenRange.value?.let { weeklyAverageDataModel ->
        setValueForSevenDaysRange(weeklyAverageDataModel, overAllData?.last(), sensorType)
      }


      val resMonths = mutableListOf<CalendarValuesDataModel>()
      viewModel.averageDataMonthDays.value?.let {
        val res = it.data
        for (i in res.indices)
        {
          val band = getBand(res[i].value.toInt())
          resMonths.add(CalendarValuesDataModel(res[i],band))
        }
        calendarValuesResult = resMonths.toList()

      }
    }

    viewModel.averageWeeklyData.observe(viewLifecycleOwner) { weeklyAvg ->

      setValueForAverageDailyData(weeklyAvg)
//      setValueForSevenDaysRange(weeklyAvg, overAllData?.last(), sensorType)
//
//      if (overAllData?.last()?.values?.get(sensorType) != null && overAllData?.last()?.values?.get(sensorType) != "N/A") {
//      val overallDataValue = overAllData?.last()?.values?.get(sensorType)?.toDouble()
//      val band = overallDataValue?.let { value -> getBand(value.toInt()) }
//      bandValueOverallData = band?.legendColor
//      }else{
//        bandValueOverallData = null
//      }
    }

    viewModel.averageDataGivenRange.observe(viewLifecycleOwner) { weeklyAvg ->

      Log.d("TINA",weeklyAvg.data.toString())
      setValueForAverageDailyData(weeklyAvg)
      setValueForSevenDaysRange(weeklyAvg, overAllData?.last(), sensorType)

      if (overAllData?.last()?.values?.get(sensorType) != null && overAllData?.last()?.values?.get(sensorType) != "N/A") {
        val overallDataValue = overAllData?.last()?.values?.get(sensorType)?.toDouble()
        val band = overallDataValue?.let { value -> getBand(value.toInt()) }
        bandValueOverallData = band?.legendColor
      }else{
        bandValueOverallData = null
      }
    }

    viewModel.averageDataGivenRange.observe(viewLifecycleOwner) {
      Log.d("Tina",it.data.toString())
    }

    viewModel.averageDataMonthDays.observe(viewLifecycleOwner){
      val res = it.data
      val resMonths = mutableListOf<CalendarValuesDataModel>()
      for (i in res.indices)
      {
        val band = getBand(res[i].value.toInt())
        resMonths.add(CalendarValuesDataModel(res[i],band))
      }
      calendarValuesResult = resMonths.toList()
    }


    viewModel.isSpecificSensorSelected.observe(viewLifecycleOwner) {
      pastWeekDataLabelForCityName = it
    }

    viewModel.dataDefinitionDataPublicHelper.value?.let {
      dataDef = it
    }
    viewModel.dataDefinitionDataPublicHelper.observe(viewLifecycleOwner) {
      dataDef = it
      displayUnit()
    }


    viewModel.bottomSheetPeek.observe(viewLifecycleOwner) { peekViewModel ->
      displayPeekContent(peekViewModel)
    }
    viewModel.graphData.observe(viewLifecycleOwner) { showGraphData(it) }
    viewModel.showNoSensorsFavourited.observe(viewLifecycleOwner) {
      bottomSheetNoSensorsContainer.visibility = if (it == true) View.VISIBLE else View.GONE
    }
    viewModel.favouriteSensorsPicking.observe(viewLifecycleOwner) { (favouriteSensors, otherSensors) ->
      val editFavouriteSensorsClickListener = View.OnClickListener {
        showFavouriteSensorsPicker(
          favouriteSensors,
          otherSensors
        ) { newSelectedFavourites, unselectedFavourites ->
          unselectedFavourites.forEach { sensor ->
            viewModel.removeFromFavoriteSensors(sensor)
          }
          newSelectedFavourites.forEach { sensor ->
            viewModel.addToFavoriteSensors(sensor)
          }
        }
      }
      selectSensorsButton.setOnClickListener(editFavouriteSensorsClickListener)
      editSelectedSensors.setOnClickListener(editFavouriteSensorsClickListener)
      sensorFavouriteButtonOverlay.setOnClickListener {
        Tooltip.Builder(requireContext())
          .anchor(sensorFavouriteButtonOverlay, follow = true)
          .text(R.string.tooltip_maximum_sensors_reached)
          .closePolicy(ClosePolicy.TOUCH_ANYWHERE_NO_CONSUME)
          .showDuration(TimeUnit.SECONDS.toMillis(1))
          .arrow(false)
          .overlay(false)
          .create()
          .show(bottomSheetSensorOverview, Tooltip.Gravity.LEFT)
      }
    }

    // Data observers
    viewModel.overallBannerData.observe(viewLifecycleOwner, overallBanner)
    map.getMapAsync { googleMap ->
      googleMap.clear()
      googleMap.applyPulseStyling(requireContext())
      viewModel.preferences.observe(viewLifecycleOwner) {
        googleMap.pulseMapType = it.mapType
      }
      googleMap.updateForCity(city)
      googleMap.lifecycleAwareOnMapClickListener(viewLifecycleOwner) {
        viewModel.deselectSensor()
      }
    }

    // Bottom sheet behavioral stuff
    val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
    bottomSheetBehavior.onStateChange(arrayOf(
      dimOnExpand(bottomSheetBackgroundOverlay),
      onExpanded {
        viewModel.loadHistoricalReadings(false)
        displayUnit()
        setDaysNames()
        viewModel.averageWeeklyData.value?.let {
          setValueForAverageDailyData(it)
        }
      }))
    peekPull.setOnClickListener { bottomSheetBehavior.toggle() }
    bottomSheetBackgroundOverlay.setOnClickListener { bottomSheetBehavior.toggle()
    }

    // Pulse loading
    viewModel.showLoading.observe(viewLifecycleOwner, loadingIndicator)

    mapLayersPick.setOnClickListener { anchor ->
      mapPreferencesPopup.toggleShown(anchor)
    }

    viewModel.preferences.observe(viewLifecycleOwner, mapPreferencesPopup)

    mapPreferencesPopup.onPreferenceChange {
      viewModel.updatePreference(it)
    }

    viewModel.selectedSensor.observe(viewLifecycleOwner, Observer {
      setValueForAverageDailyData(viewModel.averageWeeklyData.value)
      setDaysNames()
    })

    overallBanner.onToggled { isOpen ->
      TransitionManager.beginDelayedTransition(mapConstraintLayout)
      ConstraintSet().apply {
        clone(mapConstraintLayout)
        this.connect(
          R.id.mapLayersPick, ConstraintSet.TOP, R.id.overallBannerView,
          if (isOpen) ConstraintSet.BOTTOM else ConstraintSet.TOP
        )
      }.applyTo(mapConstraintLayout)
    }

  }

  private fun getBand(intValue: Int): Band {
    return dataDef.findBandByValue(intValue)
  }

  private fun displayUnit() {
    val tvUnit = view?.findViewById<TextView>(R.id.tvUnit)
    if (pastWeekDataLabelForCityName) {
      tvUnit?.text = resources.getString(
        R.string.past_week_for_whole_city,
        mainViewModel.activeCity.value?.name?.capitalize(Locale.getDefault()),
        dataDef.unit
      )
    } else {
      tvUnit?.text = resources.getString(R.string.past_week_for_specific_sensor, dataDef.unit)
    }
  }

  private fun setValuesForOverallBannerData(title: String,backgroundColor: Int,value:String,valueUnit:String,description:String,legend: Legend){
    overallBannerView.title.text = title
    overallBannerView.setCardBackgroundColor(backgroundColor)
    overallBannerView.value.text = value
    overallBannerView.valueUnit.text = valueUnit
    overallBannerView.description.text = description
    overallBannerView.legendView.legend = legend
  }

  private fun setMapValuesSensorValuesDaysRange() {
    map.getMapAsync { googleMap ->
      googleMap.clear()
      googleMap.applyPulseStyling(requireContext())
      viewModel.preferences.observe(viewLifecycleOwner) {
        googleMap.pulseMapType = it.mapType
      }
      viewModel.mapMarkersSensorReadingDaysRange.observe(viewLifecycleOwner){
        mapMarkersController.showMarkers(it?: emptyList())
      }

      val previousPolygons: MutableList<Polygon> = ArrayList()
      viewModel.mapPolygonsSensorReadingDaysRange.observe(viewLifecycleOwner) { mapPolygons ->
        previousPolygons.forEach { it.remove() }
        previousPolygons.clear()
        mapPolygons.mapTo(previousPolygons) { googleMap.addPolygon(it) }
      }

      googleMap.lifecycleAwareOnMapClickListener(viewLifecycleOwner) {
        viewModel.deselectSensor()
      }
    }
  }

  private fun setMapValuesToday() {
    map.getMapAsync { googleMap ->
      googleMap.clear()
      googleMap.applyPulseStyling(requireContext())
      viewModel.preferences.observe(viewLifecycleOwner) {
        googleMap.pulseMapType = it.mapType
      }
      viewModel.mapMarkers.observe(viewLifecycleOwner)
      { mapMarkersController.showMarkers(it ?: emptyList()) }

      val previousPolygons: MutableList<Polygon> = ArrayList()
      viewModel.mapPolygons.observe(viewLifecycleOwner) { mapPolygons ->
        previousPolygons.forEach { it.remove() }
        previousPolygons.clear()
        mapPolygons.mapTo(previousPolygons) { googleMap.addPolygon(it) }
      }
      googleMap.lifecycleAwareOnMapClickListener(viewLifecycleOwner) {
        viewModel.deselectSensor()
      }
    }
  }

  private fun setValueForSevenDaysRange(dataModel: AverageWeeklyDataModel, todayButtonData: CityOverall?, mesType: MeasurementType) {


    historyAndForecastRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    historyForecastAdapter = HistoryForecastAdapter(requireContext(), getDateButtonsList(dataModel, todayButtonData, mesType))
    historyAndForecastRecyclerView.adapter = historyForecastAdapter
    historyAndForecastRecyclerView.scrollToPosition(getDateButtonsList(dataModel,todayButtonData,mesType).size - 1)
    val today = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd")

    historyForecastAdapter.onItemClick = {
      viewModel.getSensorValues(mesType).observe(viewLifecycleOwner) {

        val bannerData =  viewModel.createAverageOverallBannerData(HistoryForecastAdapter.selectedSensorReading!!,dataDef)
        setValuesForOverallBannerData(bannerData.title,bannerData.backgroundColor, bannerData.value,bannerData.valueUnit,bannerData.description,bannerData.legend)

        setMapValuesSensorValuesDaysRange()

        if (formatter.format(HistoryForecastAdapter.TIME_STAMP) == formatter.format(today)) {
          val bannerData =  viewModel.createAverageOverallBannerData(HistoryForecastAdapter.selectedSensorReading!!,dataDef)
          setValuesForOverallBannerData(bannerData.title,bannerData.backgroundColor, bannerData.value,bannerData.valueUnit,bannerData.description,bannerData.legend)

          setMapValuesToday()
        }
      }
    }

  }


  private fun getDateButtonsList(dataModel: AverageWeeklyDataModel, todayButtonData: CityOverall?, mesType: MeasurementType): ArrayList<HistoryForecastDataModel> {

    val list = ArrayList<HistoryForecastDataModel>()
    val cal = Calendar.getInstance()
    val todayDate = cal.time
    var data: Double = -1.0

    list.add(HistoryForecastDataModel(null, null, HistoryForecastAdapter.VIEW_TYPE_EXPLORE))
    for (i in 1 until dataModel.data.size) {
      val band = getBand(dataModel.data[i].value.toInt())
      list.add(HistoryForecastDataModel(dataModel.data[i],band, HistoryForecastAdapter.VIEW_TYPE_DATE))
    }

    if (todayButtonData?.values?.get(sensorType) != null && todayButtonData.values[sensorType]!! != "N/A") {
      data = todayButtonData.values[sensorType]?.toDouble() !!
    }

    todayButtonData?.values?.get(sensorType)?.let{
      val sensorReadingFromOverall = SensorReading(todayButtonData.cityName, todayDate, mesType, "", data)
      val bandToday = getBand(sensorReadingFromOverall.value.toInt())
      list.add(HistoryForecastDataModel(sensorReadingFromOverall, bandToday, HistoryForecastAdapter.VIEW_TYPE_DATE))
    }
    return list
  }


  private fun setValueForAverageDailyData(dataModel: AverageWeeklyDataModel?) {
    val listWeeklyAverageValues = getBoundValues()
    val listWeeklyAverageColors = getBoundColors()
    setInitialDataToNotAvailable(listWeeklyAverageValues, listWeeklyAverageColors)

    dataModel?.data?.let { readings ->
      readings.forEach { sensorReading ->
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat(Constants.MONTH_DAY_YEAR_DATE_FORMAT,
          Locale(getLanguage(context)))
        val dateOfSensorToString = format.format(sensorReading.stamp)
        cal.add(Calendar.DATE, -7)
        for (i in 0..6) {
          val iteratingDate = format.format(cal.time)
          if (dateOfSensorToString == iteratingDate) {
            listWeeklyAverageValues[i].text = sensorReading.value.toInt().toString()
            val band = getBand(sensorReading.value.toInt())
            listWeeklyAverageValues[i].setTextColor(band.legendColor)
            listWeeklyAverageColors[i].setBackgroundColor(band.legendColor)
          }
          cal.add(Calendar.DATE, 1)
        }
      }
      progressBarForWeeklyAverageData.visibility = View.GONE
      weeklyAverageView.visibility = View.VISIBLE
    }
  }

  private fun setDaysNames() {
    val cal = Calendar.getInstance()
    val listOfDaysNames = getBoundsDays()
    val formatter = SimpleDateFormat("EEE", Locale(getLanguage(context)))
    listOfDaysNames.forEach {
      cal.add(Calendar.DATE, -1)
      it.text = formatter.format(cal.time)
    }
  }

  private fun getLanguage(context: Context?): String {
    return context?.getSharedPreferences(Constants.LANGUAGE_CODE, Context.MODE_PRIVATE)?.getString(Constants.LANGUAGE_CODE, "en") ?: "en"
  }

  private fun getBoundsDays(): List<TextView> {
    return listOf(nameOneDayAgo, nameTwoDaysAgo, nameThreeDaysAgo, nameFourDaysAgo,
      nameFiveDaysAgo, nameSixDaysAgo, nameSevenDaysAgo)
  }

  private fun getBoundColors(): List<View> {
    return listOf(colorSevenDaysAgo, colorSixDaysAgo, colorFiveDaysAgo,
      colorFourDaysAgo, colorThreeDaysAgo, colorTwoDaysAgo, colorOneDayAgo)
  }

  private fun getBoundValues(): List<TextView> {
    return listOf(valueSevenDaysAgo, valueSixDaysAgo, valueFiveDaysAgo,
      valueFourDaysAgo, valueThreeDaysAgo, valueTwoDaysAgo, valueOneDayAgo)
  }

  private fun setInitialDataToNotAvailable(
    listWeeklyAverageValues: List<TextView>,
    listWeeklyAverageColors: List<View>
  ) {
    for (i in listWeeklyAverageValues.indices) {
      listWeeklyAverageValues[i].text = resources.getString(R.string.not_available)
      listWeeklyAverageValues[i].setTextColor(Color.GRAY)
      listWeeklyAverageColors[i].setBackgroundColor(Color.GRAY)
    }
  }

  private fun displayPeekContent(peekViewModel: BottomSheetPeekViewModel) {
    val shownSensorOverview = displaySensorOverview(peekViewModel)
    if (shownSensorOverview) {
      noSensorsSelected.gone()
      hasSelectedSensors.gone()
    } else {
      noSensorsSelected.visible(!peekViewModel.hasFavouriteSensors)
      hasSelectedSensors.visible(peekViewModel.hasFavouriteSensors)
    }
    updatePeekHeight()
  }

  private fun displaySensorOverview(peekViewModel: BottomSheetPeekViewModel): Boolean {
    val sensorOverviewModel = peekViewModel.sensorOverviewModel
    if (sensorOverviewModel != null) {
      bottomSheetSensorOverview.visible()
      bottomSheetSensorOverview.sensorTitle.setCompoundDrawablesWithIntrinsicBounds(
        sensorOverviewModel.sensor.type.drawableRes ?: 0,
        0, 0, 0)
      bottomSheetSensorOverview.sensorTitle.text =
        sensorOverviewModel.sensor.description.toUpperCase(Locale.getDefault())
      bottomSheetSensorOverview.sensorMeasurement.text =
        sensorOverviewModel.measurement.roundToInt().toString()
      bottomSheetSensorOverview.sensorMeasurementUnit.text = sensorOverviewModel.measurementUnit
      bottomSheetSensorOverview.sensorMeasurementTime.text =
        SimpleDateFormat("HH:mm", Locale.US).format(sensorOverviewModel.timestamp)
      bottomSheetSensorOverview.sensorMeasurementDate.text =
        SimpleDateFormat("dd.MM.yyyy", Locale.US).format(sensorOverviewModel.timestamp)

      bottomSheetSensorOverview.sensorFavouriteButton.isLiked = sensorOverviewModel.favourite
      if (!peekViewModel.canAddMoreFavouriteSensors && !sensorOverviewModel.favourite) {
        bottomSheetSensorOverview.sensorFavouriteButton.isEnabled = false
        bottomSheetSensorOverview.sensorFavouriteButtonOverlay.visible()
      } else {
        bottomSheetSensorOverview.sensorFavouriteButton.isEnabled = true
        bottomSheetSensorOverview.sensorFavouriteButtonOverlay.gone()
        bottomSheetSensorOverview.sensorFavouriteButton.setOnLikeListener(object : OnLikeListener {
          override fun liked(likeButton: LikeButton?) {
            viewModel.addToFavoriteSensors(sensorOverviewModel.sensor)
          }

          override fun unLiked(likeButton: LikeButton?) {
            viewModel.removeFromFavoriteSensors(sensorOverviewModel.sensor)
          }
        })
      }
      return true
    } else {
      bottomSheetSensorOverview.gone()
      return false
    }
  }

  private fun showGraphData(it: GraphModel?) {
    if (it != null) {
      graphView.setGraphModel(it)
    } else {
      graphView.clearGraph()
    }
  }

  override fun onStart() {
    super.onStart()
    viewModel.refreshData(false)
    map.onStart()
  }

  override fun onResume() {
    super.onResume()
    map.onResume()
  }

  private fun updatePeekHeight() {
    peekContainer.post {
      map?.getMapAsync { googleMap ->
        if (bottomSheetContainer != null) {
          val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
          ValueAnimator.ofInt(bottomSheetBehavior.peekHeight, peekContainer.height).apply {
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
              bottomSheetBehavior.peekHeight = it.animatedValue as Int
              googleMap.setPadding(0, 0, 0, it.animatedValue as Int)
              if (crowdsourcingDisclaimerText != null) {
                val params = crowdsourcingDisclaimerText.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(0, 0, 8, it.animatedValue as Int + 8)
                crowdsourcingDisclaimerText.layoutParams = params
                crowdsourcingDisclaimerText.setOnClickListener { view ->
                  showDisclaimerDialog(view.context)
                }
              }
            }
          }.start()
        }
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    map.onSaveInstanceState(outState)
    super.onSaveInstanceState(outState)
  }

  override fun onPause() {
    map.onPause()
    super.onPause()
  }

  override fun onStop() {
    map.onStop()
    super.onStop()
  }
}

data class MeasurementOverviewModel(val measurement: Double, val dataDefinition: DataDefinition)