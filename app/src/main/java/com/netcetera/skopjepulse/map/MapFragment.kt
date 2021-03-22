package com.netcetera.skopjepulse.map

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.transition.TransitionManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Polygon
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.like.LikeButton
import com.like.OnLikeListener
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.PulseLoadingIndicator
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.BaseFragment
import com.netcetera.skopjepulse.base.model.*
import com.netcetera.skopjepulse.base.utils.*
import com.netcetera.skopjepulse.extensions.*
import com.netcetera.skopjepulse.favouritesensors.showFavouriteSensorsPicker
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.map.mapvisualization.MapMarkersController
import com.netcetera.skopjepulse.map.model.AverageWeeklyDataModel
import com.netcetera.skopjepulse.map.model.BottomSheetPeekViewModel
import com.netcetera.skopjepulse.map.model.GraphModel
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
import kotlinx.android.synthetic.main.map_fragment_layout.*
import kotlinx.android.synthetic.main.map_loading_indicator.*
import kotlinx.android.synthetic.main.overall_banner_layout.*
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
  private val mainViewModel : MainViewModel by sharedViewModel()

  lateinit var dataDef:DataDefinition
  lateinit var sensorType:MeasurementType

  private var pastWeekDataLabelForCityName:Boolean = true

  val city : City by lazy { arguments!!.getParcelable<City>("city")!! }

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
  }
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.map_fragment_layout, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    map.onCreate(savedInstanceState)

    // Declarations and interactions
    mapMarkersController = MapMarkersController(map) { viewModel.selectSensor(it) }

    /* Observe on what Measurement Type to show */
    mainViewModel.activeMeasurementType.observe(viewLifecycleOwner, Observer {
      viewModel.showDataForMeasurementType(it)
      sensorType = it
      progressBarForWeeklyAverageData.visibility = View.VISIBLE
      weeklyAverageView.visibility = View.GONE
      setDaysNames()
      viewModel.averageWeeklyData.value?.let { weeklyAverageDataModel ->
        setValueForAverageDailyData(weeklyAverageDataModel)
      }
    })

    viewModel.averageWeeklyData.observe(viewLifecycleOwner) {
        setValueForAverageDailyData(it)
    }

    viewModel.pastWeekCityNameLabelBoolean.observe(viewLifecycleOwner, Observer {
      pastWeekDataLabelForCityName = it
    })

    viewModel.dataDefinitionDataPublicHelper.observe(viewLifecycleOwner, Observer {
      dataDef = it
      displayUnit()
    })
    // Data observers
    viewModel.overallData.observe(viewLifecycleOwner, overallBanner)
    map.getMapAsync { googleMap ->
      googleMap.applyPulseStyling(requireContext())
      viewModel.preferences.observe(viewLifecycleOwner, Observer {
        googleMap.pulseMapType = it.mapType
      })
      googleMap.updateForCity(city)
      viewModel.mapMarkers.observe(viewLifecycleOwner, Observer { mapMarkersController.showMarkers(it ?: emptyList()) })

      val previousPolygons: MutableList<Polygon> = ArrayList()
      viewModel.mapPolygons.observe(viewLifecycleOwner, Observer { mapPolygons ->
        previousPolygons.forEach { it.remove() }
        previousPolygons.clear()
        mapPolygons.mapTo(previousPolygons) { googleMap.addPolygon(it) }
      })


      googleMap.lifecycleAwareOnMapClickListener(viewLifecycleOwner, GoogleMap.OnMapClickListener {
        viewModel.deselectSensor()
      })
    }

    viewModel.bottomSheetPeek.observe(viewLifecycleOwner, Observer { peekViewModel -> displayPeekContent(peekViewModel) })
    viewModel.graphData.observe(viewLifecycleOwner, Observer { showGraphData(it) })
    viewModel.showNoSensorsFavourited.observe(viewLifecycleOwner, Observer {
      bottomSheetNoSensorsContainer.visibility = if (it == true) View.VISIBLE else View.GONE
    })
    viewModel.favouriteSensorsPicking.observe(viewLifecycleOwner, Observer { (favouriteSensors, otherSensors) ->
      val editFavouriteSensorsClickListener = View.OnClickListener {
        showFavouriteSensorsPicker(favouriteSensors, otherSensors) { newSelectedFavourites, unselectedFavourites ->
          unselectedFavourites.forEach { unfavouritedSensor ->
            viewModel.unFavouriteSensor(unfavouritedSensor)
          }
          newSelectedFavourites.forEach { newFavouritedSensor ->
            viewModel.favouriteSensor(newFavouritedSensor)
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
    })


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
      }
    )
    )
    peekPull.setOnClickListener { bottomSheetBehavior.toggle() }
    bottomSheetBackgroundOverlay.setOnClickListener { bottomSheetBehavior.toggle() }

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
        this.connect(R.id.mapLayersPick, ConstraintSet.TOP, R.id.overallBannerView,
            if(isOpen) ConstraintSet.BOTTOM else ConstraintSet.TOP)
      }.applyTo(mapConstraintLayout)
    }

  }

  private fun getBand(intValue: Int): Band? {
    return dataDef.findBandByValue(intValue)
  }

  private fun displayUnit() {
    val tvUnit = view?.findViewById<TextView>(R.id.tvUnit)
    if (pastWeekDataLabelForCityName){
      tvUnit?.text = resources.getString(R.string.past_week_for_whole_city, mainViewModel.activeCity.value?.name?.capitalize(), dataDef.unit)
    }else {
      tvUnit?.text = resources.getString(R.string.past_week_for_specific_sensor, dataDef.unit)
    }
  }

  private fun setValueForAverageDailyData(dataModel: AverageWeeklyDataModel?) {
    val cal = Calendar.getInstance()
    val listOfTextViewsForValues = listOf(valueForMonday, valueForTuesday, valueForWednesday, valueForThursday, valueForFriday, valueForSaturday, valueForSunday)
    val listOfRectangleViews = listOf(rectangle_1, rectangle_2, rectangle_3, rectangle_4, rectangle_5, rectangle_6, rectangle_7)

    setInitialDataToNotAvailable()

    val format = SimpleDateFormat(Constants.MONTH_DAY_YEAR_DATE_FORMAT)
    dataModel?.data?.let { readings ->
      readings.forEach { sensorReading ->
        val dateOfSensorToString = sensorReading.getMonthAndDayFromStamp() + " " + sensorReading.getYearFromStamp()
        cal.add(Calendar.DATE, -7)
        for (i in 0..6) {
          val iteratingDate = format.format(cal.time)
          if (dateOfSensorToString == iteratingDate) {
            listOfTextViewsForValues[i].text = sensorReading.value.toInt().toString()
            val band = getBand(sensorReading.value.toInt())
            listOfTextViewsForValues[i].setTextColor(band!!.legendColor)
            listOfRectangleViews[i].setBackgroundColor(band.legendColor)
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
    val listOfDaysNames = listOf(tv_OneDayAgo, tv_TwoDaysAgo, tv_ThreeDaysAgo, tv_FourDaysAgo, tv_FiveDaysAgo, tv_sixDaysAgo, tv_sevenDaysAgo)
    listOfDaysNames.forEach{
      cal.add(Calendar.DATE, -1)
      it.text = SimpleDateFormat("EEE", Locale.ENGLISH).format(cal.time)
    }
  }

  private fun setInitialDataToNotAvailable(){
    val listOfTextViewsForValues = listOf(valueForMonday, valueForTuesday, valueForWednesday, valueForThursday, valueForFriday, valueForSaturday, valueForSunday)
    val listOfRectangleViews = listOf(rectangle_1, rectangle_2, rectangle_3, rectangle_4, rectangle_5, rectangle_6, rectangle_7)

    listOfTextViewsForValues.forEach{
      it.text = resources.getString(R.string.data_not_available)
      it.setTextColor(Color.GRAY)
    }
    listOfRectangleViews.forEach{
      it.setBackgroundColor(Color.GRAY)
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
      bottomSheetSensorOverview.sensorTitle.text = sensorOverviewModel.sensor.description.toUpperCase()
      bottomSheetSensorOverview.sensorMeasurement.text = sensorOverviewModel.measurement.roundToInt().toString()
      bottomSheetSensorOverview.sensorMeasurementUnit.text = sensorOverviewModel.measurementUnit
      bottomSheetSensorOverview.sensorMeasurementTime.text = SimpleDateFormat("HH:mm", Locale.US).format(sensorOverviewModel.timestamp)
      bottomSheetSensorOverview.sensorMeasurementDate.text = SimpleDateFormat("dd.MM.yyyy", Locale.US).format(sensorOverviewModel.timestamp)

      bottomSheetSensorOverview.sensorFavouriteButton.isLiked = sensorOverviewModel.favourite
      if (!peekViewModel.canAddMoreFavouriteSensors && !sensorOverviewModel.favourite) {
        bottomSheetSensorOverview.sensorFavouriteButton.isEnabled = false
        bottomSheetSensorOverview.sensorFavouriteButtonOverlay.visible()
      } else {
        bottomSheetSensorOverview.sensorFavouriteButton.isEnabled = true
        bottomSheetSensorOverview.sensorFavouriteButtonOverlay.gone()
        bottomSheetSensorOverview.sensorFavouriteButton.setOnLikeListener(object : OnLikeListener {
          override fun liked(likeButton: LikeButton?) {
            viewModel.favouriteSensor(sensorOverviewModel.sensor)
          }

          override fun unLiked(likeButton: LikeButton?) {
            viewModel.unFavouriteSensor(sensorOverviewModel.sensor)
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
      map.getMapAsync { googleMap ->
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        ValueAnimator.ofInt(bottomSheetBehavior.peekHeight, peekContainer.height).apply {
          duration = 100
          interpolator = AccelerateDecelerateInterpolator()
          addUpdateListener {
            bottomSheetBehavior.peekHeight = it.animatedValue as Int
            googleMap.setPadding(0, 0, 0, it.animatedValue as Int)
            val params = crowdsourcingDisclaimerText.layoutParams as ConstraintLayout.LayoutParams
            params.setMargins(0, 0, 8, it.animatedValue as Int + 8)
            crowdsourcingDisclaimerText.layoutParams = params
            crowdsourcingDisclaimerText.setOnClickListener { view ->
              showDisclaimerDialog(view.context)
            }
          }
        }.start()
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