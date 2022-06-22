package com.netcetera.skopjepulse.map

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TableLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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
import com.netcetera.skopjepulse.historyforecast.CalendarUtils
import com.netcetera.skopjepulse.historyforecast.calendar.*
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
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

  private val formatterLocalDate: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")

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
    var bandValueOverallData: Int? = null
    var toDate  = LocalDate.parse("22/06/2022", CalendarDialog.formatter)
    var fromDate = toDate.minusDays(8)

    var DATE_MONTH_REQUEST: LocalDate = CalendarAdapter.DATE_INPUT_TODAY
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
      progressBarForWeeklyAverageData.visibility = View.VISIBLE
      weeklyAverageView.visibility = View.GONE
      setDaysNames()

      viewModel.overallBannerData.observe(viewLifecycleOwner, overallBanner)
      setMapValuesToday()


      viewModel.averageWeeklyData.value?.let { weeklyAverageDataModel ->
        setValueForAverageDailyData(weeklyAverageDataModel)
      }
      viewModel.averageDataGivenRange.value?.let { weeklyAverageDataModel ->
        setValueForSevenDaysRange(weeklyAverageDataModel.data, overAllData?.last(), sensorType)
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
    }

    viewModel.averageDataGivenRange.observe(viewLifecycleOwner) { weeklyAvg ->
      setValueForAverageDailyData(weeklyAvg)
      setValueForSevenDaysRange(weeklyAvg.data, overAllData?.last(), sensorType)

      if (overAllData?.last()?.values?.get(sensorType) != null && overAllData?.last()?.values?.get(sensorType) != "N/A") {
        val overallDataValue = overAllData?.last()?.values?.get(sensorType)?.toDouble()
        val band = overallDataValue?.let { value -> getBand(value.toInt()) }
        bandValueOverallData = band?.legendColor
      }else{
        bandValueOverallData = null
      }
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

  fun check(){

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
  private  fun nextMonthClick(context: Context,dateInput: LocalDate?, calendarMonthYearText:TextView,recyclerView:RecyclerView,calendarNextArrow:TextView,
                              calendarNextArrowUnavailable:TextView,calendarPreviousArrow:TextView,alertDialog: AlertDialog) {

    if (dateInput != null){
      val next = dateInput.plusMonths(1)
      val nextDow = next.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
      val lengthOfNextMonth = next.lengthOfMonth()
      val intValueDow = CalendarUtils.intValueForDayOfWeek(nextDow)
      CalendarAdapter.DATE_INPUT = next
      DATE_MONTH_REQUEST = next

      val month = CalendarAdapter.DATE_INPUT?.month.toString()
      val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
      calendarMonthYearText.text = "${CalendarUtils.getMonthByLanguage(context, monthFirstUpper)} ${CalendarAdapter.DATE_INPUT?.year}"

      val list = ArrayList<CalendarItemsDataModel>()
      for (i in 0 until intValueDow) {
        list.add(CalendarItemsDataModel(0, intValueDow))
      }
      for (j in 1..lengthOfNextMonth) {
        list.add(CalendarItemsDataModel(j, intValueDow))
      }

      setCalendarAdapter(requireContext(),list,recyclerView,alertDialog, calendarValuesResult)

      if((CalendarAdapter.DATE_INPUT_TODAY.month.value > CalendarAdapter.DATE_INPUT!!.month.value && CalendarAdapter.DATE_INPUT_TODAY.year > CalendarAdapter.DATE_INPUT!!.year)
        || (CalendarAdapter.DATE_INPUT_TODAY.month.value == CalendarAdapter.DATE_INPUT!!.month.value&& CalendarAdapter.DATE_INPUT_TODAY.year == CalendarAdapter.DATE_INPUT!!.year)){
        calendarNextArrow.visibility = View.GONE
        calendarNextArrowUnavailable.visibility = View.VISIBLE
      }

      calendarPreviousArrow.setOnClickListener {
        previousMonthAverageValues(recyclerView, alertDialog)
        update(context,next,calendarMonthYearText,recyclerView,calendarNextArrow,calendarNextArrowUnavailable,calendarPreviousArrow,alertDialog)
      }

      calendarNextArrow.setOnClickListener {
        nextMonthAverageValues(recyclerView, alertDialog)
        nextMonthClick(context,next,calendarMonthYearText,recyclerView,calendarNextArrow,calendarNextArrowUnavailable,calendarPreviousArrow,alertDialog)
      }
    }
  }

  private fun setCalendarAdapter(context: Context, list: ArrayList<CalendarItemsDataModel>,recyclerView: RecyclerView,alertDialog: AlertDialog,values: List<CalendarValuesDataModel>) {
    val adapter = CalendarAdapter(context, list, CalendarAdapter.DATE_INPUT_TODAY, values, bandValueOverallData)
    recyclerView.adapter = adapter

    adapter.onItemClick = {
      val clickedDate = LocalDate.parse(CalendarAdapter.DATE_CLICKED, CalendarDialog.formatter)
      val fromClickedDate = clickedDate.plusDays(4)
      toDate = fromClickedDate
      fromDate = toDate.minusDays(8)
      viewModel.getAvgDataRangeGiven(sensorId = null, sensorType, fromDate, toDate).observe(viewLifecycleOwner) {
          setValueForSevenDaysRange(it.data!!, overAllData?.last(), sensorType)
        //viewModel.refreshData(true)
        }
      alertDialog.dismiss()
    }
  }

  private fun update(context: Context,date: LocalDate?,calendarMonthYearText:TextView,recyclerView:RecyclerView,calendarNextArrow:TextView,
                     calendarNextArrowUnavailable:TextView,calendarPreviousArrow:TextView,alertDialog: AlertDialog) {
    if (date == null) {
      val date = LocalDate.parse("01/06/2022", formatterLocalDate)
      CalendarAdapter.DATE_INPUT = date
      val dow: DayOfWeek = date.dayOfWeek
      val lengthOfMonth = date.lengthOfMonth()
      val output: String = dow.getDisplayName(TextStyle.SHORT, Locale.US)
      val intValueDow = CalendarUtils.intValueForDayOfWeek(output)

      calendarNextArrow.visibility = View.GONE
      calendarNextArrowUnavailable.visibility = View.VISIBLE

      val month = CalendarAdapter.DATE_INPUT?.month.toString()
      val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
      calendarMonthYearText.text = "${CalendarUtils.getMonthByLanguage(context, monthFirstUpper)} ${CalendarAdapter.DATE_INPUT?.year}"


      val list = ArrayList<CalendarItemsDataModel>()
      for (i in 0 until intValueDow) {
        list.add(CalendarItemsDataModel(0, intValueDow))
      }
      for (j in 1..lengthOfMonth) {
        list.add(CalendarItemsDataModel(j, intValueDow))
      }

      setCalendarAdapter(requireContext(),list,recyclerView,alertDialog, calendarValuesResult)
    }else {

      calendarNextArrow.visibility = View.VISIBLE
      calendarNextArrowUnavailable.visibility = View.GONE
      recyclerView.visibility = View.GONE
      calendarMonthYearText.visibility = View.GONE
      val prev = date.minusMonths(1)

      CalendarAdapter.DATE_INPUT = prev
      DATE_MONTH_REQUEST = prev

      val prevDay = prev.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)

      val lengthOfMonth = prev?.lengthOfMonth()
      val dayOfWeekIntValue = CalendarUtils.intValueForDayOfWeek(prevDay)

      val list = ArrayList<CalendarItemsDataModel>()
      for (i in 0 until dayOfWeekIntValue) {
        list.add(CalendarItemsDataModel(0, dayOfWeekIntValue))
      }
      for (j in 1..lengthOfMonth!!) {
        list.add(CalendarItemsDataModel(j, dayOfWeekIntValue))
      }
      setCalendarAdapter(requireContext(),list, recyclerView, alertDialog, calendarValuesResult)

      val month = CalendarAdapter.DATE_INPUT?.month.toString()
      val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
      calendarMonthYearText.text = "${CalendarUtils.getMonthByLanguage(context, monthFirstUpper)} ${CalendarAdapter.DATE_INPUT?.year}"

      calendarMonthYearText.visibility = View.VISIBLE
      recyclerView.visibility = View.VISIBLE

      if ((CalendarAdapter.DATE_INPUT_TODAY.month.value > CalendarAdapter.DATE_INPUT!!.month.value && CalendarAdapter.DATE_INPUT_TODAY.year > CalendarAdapter.DATE_INPUT!!.year)
        || (CalendarAdapter.DATE_INPUT_TODAY.month.value == CalendarAdapter.DATE_INPUT!!.month.value && CalendarAdapter.DATE_INPUT_TODAY.year == CalendarAdapter.DATE_INPUT!!.year)
      ) {
        calendarNextArrow.visibility = View.GONE
        calendarNextArrowUnavailable.visibility = View.VISIBLE
      }

      calendarPreviousArrow.setOnClickListener {
        previousMonthAverageValues(recyclerView, alertDialog)
        update(context,prev,calendarMonthYearText,recyclerView, calendarNextArrow, calendarNextArrowUnavailable, calendarPreviousArrow, alertDialog)
      }
      calendarNextArrowUnavailable.visibility = View.GONE
      calendarNextArrow.visibility = View.VISIBLE

      calendarNextArrow.setOnClickListener {
        nextMonthAverageValues(recyclerView, alertDialog)
        nextMonthClick(context,prev,calendarMonthYearText,recyclerView,calendarNextArrow,calendarNextArrowUnavailable,calendarPreviousArrow,alertDialog)

      }
    }

  }
  private fun updateFromMonthYearPicker(context: Context,date: LocalDate,recyclerView: RecyclerView,calendarNextArrow:TextView,calendarNextArrowUnavailable:TextView,calendarMonthYearText:TextView
  ,calendarPreviousArrow: TextView,alertDialog: AlertDialog){
    CalendarAdapter.DATE_INPUT = date
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
    val intValueDayOfWeek = CalendarUtils.intValueForDayOfWeek(dayOfWeek)
    val lengthOfMonth = date.lengthOfMonth()

    if((CalendarAdapter.DATE_INPUT_TODAY.month.value > CalendarAdapter.DATE_INPUT!!.month.value && CalendarAdapter.DATE_INPUT_TODAY.year > CalendarAdapter.DATE_INPUT!!.year)
      || (CalendarAdapter.DATE_INPUT_TODAY.month.value == CalendarAdapter.DATE_INPUT!!.month.value&& CalendarAdapter.DATE_INPUT_TODAY.year == CalendarAdapter.DATE_INPUT!!.year)){
      calendarNextArrow.visibility = View.GONE
      calendarNextArrowUnavailable.visibility = View.VISIBLE
    }


    val listOfDaysMonth = ArrayList<CalendarItemsDataModel>()
    for (i in 0 until intValueDayOfWeek){
      listOfDaysMonth.add(CalendarItemsDataModel(0,intValueDayOfWeek))
    }
    for (j in 1..lengthOfMonth){
      listOfDaysMonth.add(CalendarItemsDataModel(j,intValueDayOfWeek))
    }

    setCalendarAdapter(requireContext(),listOfDaysMonth,recyclerView, alertDialog,
      calendarValuesResult)
    val month = CalendarAdapter.DATE_INPUT?.month.toString()
    val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
    calendarMonthYearText.text = "${CalendarUtils.getMonthByLanguage(context,monthFirstUpper)} ${CalendarAdapter.DATE_INPUT?.year}"
    calendarMonthYearText.visibility = View.VISIBLE
    recyclerView.visibility = View.VISIBLE

    calendarNextArrow.setOnClickListener {
      nextMonthAverageValues(recyclerView, alertDialog)
      nextMonthClick(context,date,calendarMonthYearText,recyclerView,calendarNextArrow,calendarNextArrowUnavailable,calendarPreviousArrow,alertDialog)
    }

  }
  private fun previousMonthAverageValues(recyclerView: RecyclerView,alertDialog: AlertDialog){
    val fromDate= CalendarAdapter.DATE_INPUT!!.minusMonths(1)
    val dow: DayOfWeek = fromDate.dayOfWeek
    val lengthOfMonth = fromDate.lengthOfMonth()
    val output: String = dow.getDisplayName(TextStyle.SHORT, Locale.US)
    val intValueDow = CalendarUtils.intValueForDayOfWeek(output)

    val list = ArrayList<CalendarItemsDataModel>()
    for (i in 0 until intValueDow) {
      list.add(CalendarItemsDataModel(0, intValueDow))
    }
    for (j in 1..lengthOfMonth) {
      list.add(CalendarItemsDataModel(j, intValueDow))
    }
    viewModel.getAvgDataMonthPreviuosMonth(sensorId = null,sensorType,fromDate).observe(viewLifecycleOwner){
      Log.d("VALUE",it.data.toString())

      val resMonths = mutableListOf<CalendarValuesDataModel>()
      val res = it.data
      for (i in res!!.indices)
      {
        val band = getBand(res[i].value.toInt())
        resMonths.add(CalendarValuesDataModel(res[i],band))
      }
      setCalendarAdapter(requireContext(),list, recyclerView, alertDialog,resMonths.toList())
    }
  }
  private fun nextMonthAverageValues(recyclerView: RecyclerView,alertDialog: AlertDialog){
    val fromDate= CalendarAdapter.DATE_INPUT!!.plusMonths(1)
    val dow: DayOfWeek = fromDate.dayOfWeek
    val lengthOfMonth = fromDate.lengthOfMonth()
    val output: String = dow.getDisplayName(TextStyle.SHORT, Locale.US)
    val intValueDow = CalendarUtils.intValueForDayOfWeek(output)

    val list = ArrayList<CalendarItemsDataModel>()
    for (i in 0 until intValueDow) {
      list.add(CalendarItemsDataModel(0, intValueDow))
    }
    for (j in 1..lengthOfMonth) {
      list.add(CalendarItemsDataModel(j, intValueDow))
    }
    viewModel.getAvgDataMonthPreviuosMonth(sensorId = null,sensorType,fromDate).observe(viewLifecycleOwner){
      Log.d("VALUE",it.data.toString())

      val resMonths = mutableListOf<CalendarValuesDataModel>()
      val res = it.data
      for (i in res!!.indices)
      {
        val band = getBand(res[i].value.toInt())
        resMonths.add(CalendarValuesDataModel(res[i],band))
      }
      setCalendarAdapter(requireContext(),list, recyclerView, alertDialog,resMonths.toList())
    }
  }
  private fun calendarCancelButton(alertDialog: AlertDialog,calendarDialogCancelButton:TextView ) {
    calendarDialogCancelButton.setOnClickListener {
      alertDialog.dismiss()
    }
  }

  private fun calendarCancelButtonFromMonthAndYearPicker(context: Context, calendarPreviousArrow: TextView, calendarNextArrow: TextView,
                                                         calendarHeader: TableLayout, calendarYearPicker: TextView, calendarMonthYearText: TextView,
                                                         calendarLine: View, recyclerView: RecyclerView, monthYearPickerRecyclerView:RecyclerView,alertDialog: AlertDialog,calendarDialogCancelButton:TextView) {
    calendarDialogCancelButton.setOnClickListener {
      CalendarUtils.showCalendarHideRecyclerView(context, calendarPreviousArrow, calendarNextArrow, calendarHeader, calendarYearPicker, calendarMonthYearText,
        calendarLine, recyclerView, monthYearPickerRecyclerView)
      calendarCancelButton(alertDialog,calendarDialogCancelButton)
    }
  }

  private fun setValueForSevenDaysRange(dataModel: List<SensorReading>, todayButtonData: CityOverall?, mesType: MeasurementType) {

    //HISTORY FORECAST ADAPTER
    historyAndForecastRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    historyForecastAdapter = HistoryForecastAdapter(requireContext(), getDateButtonsList(dataModel, todayButtonData, mesType))
    historyAndForecastRecyclerView.adapter = historyForecastAdapter
    //happens on first start and happens if today is selected in the calendar
    historyAndForecastRecyclerView.scrollToPosition(getDateButtonsList(dataModel,todayButtonData,mesType).size - 1)
    //happens when a date out of the current week has been selected
   // historyAndForecastRecyclerView.scrollToPosition(getDateButtonsList(dataModel,todayButtonData,mesType).size - 1)

    //historyAndForecastRecyclerView.findViewHolderForAdapterPosition(4)?.itemView?.performClick()

    val today = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd")

    // CALENDAR ADAPTER
    val layoutManager = object : GridLayoutManager(context, 7) {
      override fun supportsPredictiveItemAnimations(): Boolean {
        return false
      }
    }
    val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.calendar_dialog, null,false)
    val recyclerView = dialogView.findViewById<RecyclerView>(R.id.calendarRecyclerView)
    val calendarNextArrow = dialogView.findViewById<TextView>(R.id.calendarNextArrow)
    val calendarPreviousArrow = dialogView.findViewById<TextView>(R.id.calendarPreviousArrow)
    val calendarNextArrowUnavailable = dialogView.findViewById<TextView>(R.id.calendarNextArrowUnavailable)
    val calendarMonthYearText = dialogView.findViewById<TextView>(R.id.calendarMonthYearText)
    val calendarHeader = dialogView.findViewById<TableLayout>(R.id.calendarHeader)
    val calendarLine = dialogView.findViewById<View>(R.id.calendarLine)
    val calendarYearPicker = dialogView.findViewById<TextView>(R.id.calendarYearPicker)
    val calendarDialogCancelButton = dialogView.findViewById<TextView>(R.id.calendarDialogCancelButton)
    val monthYearPickerRecyclerView =  dialogView.findViewById<RecyclerView>(R.id.monthYearPickerRecyclerView)
    val alertDialog =  AlertDialog.Builder(requireContext()).setView(dialogView).create()
    recyclerView.layoutManager = layoutManager


    historyForecastAdapter.onItemClickExplore = {
      calendarNextArrowUnavailable.visibility = View.GONE
      calendarLine.visibility = View.VISIBLE
      calendarMonthYearText.visibility = View.VISIBLE
      calendarYearPicker.visibility = View.GONE
      calendarPreviousArrow.visibility = View.VISIBLE
      calendarNextArrow.visibility = View.VISIBLE
      calendarHeader.visibility = View.VISIBLE
      recyclerView.visibility = View.VISIBLE
      monthYearPickerRecyclerView.visibility = View.GONE
      calendarCancelButton(alertDialog,calendarDialogCancelButton)

      //Set calendar
      update(requireContext(),null,calendarMonthYearText, recyclerView, calendarNextArrow, calendarNextArrowUnavailable, calendarPreviousArrow, alertDialog)
      calendarPreviousArrow.setOnClickListener {
        previousMonthAverageValues(recyclerView, alertDialog)
        update(requireContext(),CalendarAdapter.DATE_INPUT,calendarMonthYearText, recyclerView, calendarNextArrow, calendarNextArrowUnavailable, calendarPreviousArrow, alertDialog)
      }
      alertDialog.show()


      val arrayOfMonths = arrayOf(
        requireContext().getString(R.string.january).substring(0, 3),
        requireContext().getString(R.string.february).substring(0, 3),
        requireContext().getString(R.string.march).substring(0, 3),
        requireContext().getString(R.string.april).substring(0, 3),
        requireContext().getString(R.string.may).substring(0, 3),
        requireContext().getString(R.string.june).substring(0, 3),
        requireContext().getString(R.string.july).substring(0, 3),
        requireContext().getString(R.string.august).substring(0, 3),
        requireContext().getString(R.string.september).substring(0, 3),
        requireContext().getString(R.string.october).substring(0, 3),
        requireContext().getString(R.string.november).substring(0, 3),
        requireContext().getString(R.string.december).substring(0, 3))

      //We have data from 2017:
      val fromData = LocalDate.parse("01/01/2017",formatterLocalDate)
      val maxYear = (fromData.year).downTo(CalendarAdapter.DATE_INPUT_TODAY.year).last

      val arrayOfYear = arrayOf("2017", "2018", "2019", "2020", "2021", "2022")

      val month = CalendarAdapter.DATE_INPUT?.month?.name
      val monthValue = CalendarAdapter.DATE_INPUT?.month?.value
      val currentYear = CalendarAdapter.DATE_INPUT?.year.toString()
      val yearValue = CalendarAdapter.DATE_INPUT?.year
      val currentMonth = month?.substring(0, 1)?.toUpperCase() + month?.substring(1)?.toLowerCase()
      calendarMonthYearText.setOnClickListener {
        calendarNextArrowUnavailable.visibility = View.GONE
        calendarLine.visibility = View.GONE
        calendarMonthYearText.visibility = View.GONE
        calendarYearPicker.visibility = View.VISIBLE
        calendarYearPicker.text = "${CalendarAdapter.DATE_INPUT?.year}"
        calendarPreviousArrow.visibility = View.GONE
        calendarNextArrow.visibility = View.GONE
        calendarHeader.visibility = View.GONE
        recyclerView.visibility = View.GONE
        monthYearPickerRecyclerView.visibility = View.VISIBLE
        val monthAdapter = CalendarMonthYearPickerAdapter(requireContext(), arrayOfMonths)
        monthYearPickerRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        monthYearPickerRecyclerView.adapter = monthAdapter
        monthYearPickerRecyclerView.suppressLayout(true)
        monthAdapter.onItemClick = {
          CalendarDialog.newMonth = CalendarUtils.getFullMonthName(requireActivity(),CalendarMonthYearPickerAdapter.MONTH_YEAR_VALUE)
          calendarLine.visibility = View.VISIBLE
          calendarYearPicker.visibility = View.GONE
          val newDate = LocalDate.parse("01/${CalendarUtils.getMonthNumber(requireContext(),CalendarDialog.newMonth!!)}/${CalendarDialog.newYear ?: yearValue}",formatterLocalDate)
          updateFromMonthYearPicker(requireContext(),newDate,recyclerView, calendarNextArrow, calendarNextArrowUnavailable, calendarMonthYearText, calendarPreviousArrow, alertDialog)

          calendarMonthYearText.visibility = View.VISIBLE
          val month = CalendarAdapter.DATE_INPUT?.month.toString()
          val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
          calendarMonthYearText.text = "${CalendarUtils.getMonthByLanguage(requireContext(),monthFirstUpper)} ${CalendarAdapter.DATE_INPUT?.year?:currentYear }"
          calendarPreviousArrow.visibility = View.VISIBLE
          calendarNextArrow.visibility = View.VISIBLE
          calendarHeader.visibility = View.VISIBLE
          recyclerView.visibility = View.VISIBLE
          monthYearPickerRecyclerView.visibility = View.GONE
          CalendarUtils.showCalendarHideRecyclerView(requireContext(),calendarPreviousArrow,calendarNextArrow,calendarHeader, calendarYearPicker, calendarMonthYearText, calendarLine, recyclerView, monthYearPickerRecyclerView)
          calendarCancelButton(alertDialog,calendarDialogCancelButton)
        }
        calendarDialogCancelButton.setOnClickListener {
          CalendarUtils.showCalendarHideRecyclerView(requireContext(),calendarPreviousArrow,calendarNextArrow,calendarHeader, calendarYearPicker, calendarMonthYearText, calendarLine, recyclerView, monthYearPickerRecyclerView)
          calendarCancelButton(alertDialog,calendarDialogCancelButton)
        }

        //Year Picker
        calendarYearPicker.setOnClickListener {
          calendarNextArrowUnavailable.visibility = View.GONE
          calendarYearPicker.visibility = View.GONE
          calendarMonthYearText.visibility = View.GONE
          calendarPreviousArrow.visibility = View.GONE
          calendarNextArrow.visibility = View.GONE
          calendarHeader.visibility = View.GONE
          recyclerView.visibility = View.GONE
          monthYearPickerRecyclerView.visibility = View.VISIBLE
          monthYearPickerRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
          val yearAdapter = CalendarMonthYearPickerAdapter(requireContext(), arrayOfYear)
          monthYearPickerRecyclerView.adapter = yearAdapter
          monthYearPickerRecyclerView.suppressLayout(true)
          yearAdapter.onItemClick = {
            CalendarDialog.newYear = CalendarMonthYearPickerAdapter.MONTH_YEAR_VALUE
            val monthAdapter = CalendarMonthYearPickerAdapter(requireContext(), arrayOfMonths)
            monthYearPickerRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            monthYearPickerRecyclerView.adapter = monthAdapter
            monthYearPickerRecyclerView.suppressLayout(true)
            monthAdapter.onItemClick = {
              CalendarDialog.newMonth = CalendarUtils.getFullMonthName(requireContext(),CalendarMonthYearPickerAdapter.MONTH_YEAR_VALUE)
              calendarLine.visibility = View.VISIBLE
              calendarYearPicker.visibility = View.GONE
              val month = CalendarAdapter.DATE_INPUT?.month.toString()
              val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
              val newInputs = "${CalendarUtils.getMonthByLanguage(requireContext(),monthFirstUpper)} ${CalendarAdapter.DATE_INPUT?.year}"


              val newDate = LocalDate.parse("01/${CalendarUtils.getMonthNumber(requireContext(),CalendarDialog.newMonth!!) ?: monthValue }/${CalendarDialog.newYear ?: yearValue}",formatterLocalDate)
              Log.d("New date",newDate.toString())
              updateFromMonthYearPicker(requireContext(),newDate,recyclerView, calendarNextArrow, calendarNextArrowUnavailable, calendarMonthYearText, calendarPreviousArrow, alertDialog)

              calendarMonthYearText.visibility = View.VISIBLE
              calendarMonthYearText.text = newInputs
              calendarPreviousArrow.visibility = View.VISIBLE
              calendarNextArrow.visibility = View.VISIBLE
              calendarHeader.visibility = View.VISIBLE
              recyclerView.visibility = View.VISIBLE
              monthYearPickerRecyclerView.visibility = View.GONE
              CalendarUtils.showCalendarHideRecyclerView(requireContext(),calendarPreviousArrow,calendarNextArrow,calendarHeader, calendarYearPicker, calendarMonthYearText, calendarLine, recyclerView, monthYearPickerRecyclerView)
              calendarCancelButton(alertDialog,calendarDialogCancelButton)
              monthAdapter.onItemClick ={
                calendarLine.visibility = View.VISIBLE
                calendarYearPicker.visibility = View.GONE
                calendarMonthYearText.visibility = View.VISIBLE
                val month = CalendarAdapter.DATE_INPUT?.month.toString()
                val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
                calendarMonthYearText.text = "${CalendarUtils.getMonthByLanguage(requireContext(),monthFirstUpper)} ${CalendarAdapter.DATE_INPUT?.year}"
                calendarPreviousArrow.visibility = View.VISIBLE
                calendarNextArrow.visibility = View.VISIBLE
                calendarHeader.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                monthYearPickerRecyclerView.visibility = View.GONE
                  calendarCancelButtonFromMonthAndYearPicker(requireContext(),calendarPreviousArrow,calendarNextArrow,calendarHeader,calendarYearPicker,calendarMonthYearText
                  ,calendarLine,recyclerView, monthYearPickerRecyclerView, alertDialog, calendarDialogCancelButton)
              }
            }
          }
        }
      } }

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




  private fun getDateButtonsList(dataModel:List<SensorReading>, todayButtonData: CityOverall?, mesType: MeasurementType): ArrayList<HistoryForecastDataModel> {

    val list = ArrayList<HistoryForecastDataModel>()
    val cal = Calendar.getInstance()
    val todayDate = cal.time
    var data: Double = -1.0

    list.add(HistoryForecastDataModel(null, null, HistoryForecastAdapter.VIEW_TYPE_EXPLORE))
    for (i in 1 until dataModel.size) {
      val band = getBand(dataModel[i].value.toInt())
      list.add(HistoryForecastDataModel(dataModel[i],band, HistoryForecastAdapter.VIEW_TYPE_DATE))
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