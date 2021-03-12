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
import com.netcetera.skopjepulse.PulseLoadingIndicator
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.BaseFragment
import com.netcetera.skopjepulse.base.model.*
import com.netcetera.skopjepulse.extensions.*
import com.netcetera.skopjepulse.favouritesensors.showFavouriteSensorsPicker
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.map.mapvisualization.MapMarkersController
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
  private lateinit var dataMon:TextView
  private lateinit var dataTue:TextView
  private lateinit var dataWed:TextView
  private lateinit var dataThu:TextView
  private lateinit var dataFri:TextView
  private lateinit var dataSat:TextView
  private lateinit var dataSun:TextView
  private lateinit var rectangle1:View
  private lateinit var rectangle2:View
  private lateinit var rectangle3:View
  private lateinit var rectangle4:View
  private lateinit var rectangle5:View
  private lateinit var rectangle6:View
  private lateinit var rectangle7:View

  lateinit var dataDef:DataDefinition
  lateinit var sensorType:MeasurementType

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
      setDaysNames()
      setValueForAverageDailyData()
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
    dataMon = view.findViewById(R.id.valueForMonday)
    dataTue = view.findViewById(R.id.valueForTueday)
    dataWed = view.findViewById(R.id.valueForWednesday)
    dataThu = view.findViewById(R.id.valueForThursday)
    dataFri = view.findViewById(R.id.valueForFriday)
    dataSat = view.findViewById(R.id.valueForSaturday)
    dataSun = view.findViewById(R.id.valueForSunday)

    rectangle1 = view.findViewById(R.id.rectangle_1)
    rectangle2 = view.findViewById(R.id.rectangle_2)
    rectangle3 = view.findViewById(R.id.rectangle_3)
    rectangle4 = view.findViewById(R.id.rectangle_4)
    rectangle5 = view.findViewById(R.id.rectangle_5)
    rectangle6 = view.findViewById(R.id.rectangle_6)
    rectangle7 = view.findViewById(R.id.rectangle_7)

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
        setValueForAverageDailyData()
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
      setValueForAverageDailyData()
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
    tvUnit?.text = resources.getString(R.string.past_week,dataDef.unit)
  }

  private fun setValueForAverageDailyData() {
    val cal = Calendar.getInstance()
    val arr = listOf(dataMon, dataTue, dataWed, dataThu, dataFri, dataSat, dataSun)
    val arr2 = listOf(rectangle1, rectangle2, rectangle3, rectangle4, rectangle5, rectangle6, rectangle7)

    for (value in arr){
      value.text = resources.getString(R.string.data_not_available)
      value.setTextColor(Color.GRAY)
    }
    for (i in arr2){
      i.setBackgroundColor(Color.GRAY)
    }

    val format = SimpleDateFormat("MMM dd yyyy")

    val listOfDailyAverageData = viewModel.getAverageData(sensorType).value
    if (listOfDailyAverageData != null) {

      for (c in listOfDailyAverageData) {
        val dateOfSensorToString = c.stamp.toString().substring(4, 10) + " " + c.stamp.toString().substring(30, 34)
        cal.add(Calendar.DATE, -7)
        for (i in 0..6) {
          val iteratingDate  = format.format(cal.time)
          if (dateOfSensorToString == iteratingDate) {
            arr[i].text = c.value.toInt().toString()
            val band = getBand(c.value.toInt())
            arr[i].setTextColor(band!!.legendColor)
            arr2[i].setBackgroundColor(band.legendColor)
            this.sensorType
          }
          cal.add(Calendar.DATE, 1)
        }
      }
    }
  }

  private fun setDaysNames() {
    val cal = Calendar.getInstance()
    val sevenDaysAgo :TextView = requireView().findViewById(R.id.tv_sevenDaysAgo)
    val sixDaysAgo :TextView = requireView().findViewById(R.id.tv_sixDaysAgo)
    val fiveDaysAgo :TextView = requireView().findViewById(R.id.tv_FiveDaysAgo)
    val fourDaysAgo :TextView = requireView().findViewById(R.id.tv_FourDaysAgo)
    val threeDaysAgo :TextView = requireView().findViewById(R.id.tv_ThreeDaysAgo)
    val twoDaysAgo :TextView = requireView().findViewById(R.id.tv_TwoDaysAgo)
    val oneDayAgo :TextView = requireView().findViewById(R.id.tv_OneDayAgo)
    val arr = listOf(oneDayAgo, twoDaysAgo, threeDaysAgo, fourDaysAgo, fiveDaysAgo, sixDaysAgo, sevenDaysAgo)
    for (tv in arr){
      cal.add(Calendar.DATE, -1)
      val dayOfWeek = SimpleDateFormat("EEEE", Locale.ENGLISH).format(cal.time)
      tv.text = dayOfWeek.substring(0, 3)
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