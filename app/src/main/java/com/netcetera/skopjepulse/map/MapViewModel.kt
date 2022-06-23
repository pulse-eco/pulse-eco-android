package com.netcetera.skopjepulse.map

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions
import com.netcetera.skopjepulse.R.string
import com.netcetera.skopjepulse.base.data.DataDefinitionProvider
import com.netcetera.skopjepulse.base.data.Resource
import com.netcetera.skopjepulse.base.data.repository.CityPulseRepository
import com.netcetera.skopjepulse.base.data.repository.FavouriteSensorsStorage
import com.netcetera.skopjepulse.base.data.repository.SensorReadings
import com.netcetera.skopjepulse.base.data.repository.Sensors
import com.netcetera.skopjepulse.base.model.*
import com.netcetera.skopjepulse.base.viewModel.PulseViewModel
import com.netcetera.skopjepulse.base.viewModel.toErrorLiveDataResource
import com.netcetera.skopjepulse.base.viewModel.toLoadingLiveDataResource
import com.netcetera.skopjepulse.extensions.combine
import com.netcetera.skopjepulse.extensions.interpolateColor
import com.netcetera.skopjepulse.historyforecast.calendar.CalendarAdapter
import com.netcetera.skopjepulse.historyforecast.calendar.CalendarDialog
import com.netcetera.skopjepulse.map.model.*
import com.netcetera.skopjepulse.map.overallbanner.Legend
import com.netcetera.skopjepulse.map.overallbanner.LegendBand
import com.netcetera.skopjepulse.map.overallbanner.OverallBannerData
import com.netcetera.skopjepulse.map.preferences.DataVisualization.MARKERS
import com.netcetera.skopjepulse.map.preferences.DataVisualization.VORONOI
import com.netcetera.skopjepulse.map.preferences.MapPreferencesStorage
import com.netcetera.skopjepulse.map.preferences.Preferences
import com.netcetera.skopjepulse.map.preferences.filter
import com.netcetera.skopjepulse.voronoi.voronoiCityBounds
import org.jetbrains.anko.withAlpha
import org.kynosarges.tektosyne.geometry.GeoUtils
import org.kynosarges.tektosyne.geometry.PointD
import org.kynosarges.tektosyne.geometry.PolygonLocation
import org.kynosarges.tektosyne.geometry.Voronoi
import java.time.LocalDate
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Implementation of [androidx.lifecycle.ViewModel] for the [MapFragment] that is used for
 * displaying of data of a single [City].
 */

class MapViewModel(
    city: City,
    private val dataDefinitionProvider: DataDefinitionProvider,
    favouriteSensorsStorage: FavouriteSensorsStorage,
    private val mapPreferencesStorage: MapPreferencesStorage,
    private val cityPulseRepository: CityPulseRepository) :
    PulseViewModel(cityPulseRepository, favouriteSensorsStorage) {

  private val selectedMeasurementType = MutableLiveData<MeasurementType>()
  val selectedSensor = MutableLiveData<Sensor?>().apply { value = null }
  var dataDefinitionDataPublicHelper :LiveData<DataDefinition>

  val overallBannerData: LiveData<OverallBannerData>
  val mapMarkers: LiveData<List<MapMarkerModel>>
  val mapMarkersSensorReadingDaysRange: LiveData<List<MapMarkerModel>>
  val graphData: LiveData<GraphModel>
  /**
   * True/false if there are any favourited sensors by the user for the current city.
   */
  val showNoSensorsFavourited: LiveData<Boolean>
  /**
   * Data for picking of favourite sensors.
   * [Pair.first] are the already favourited sensors.
   * [Pair.second] are the rest of the sensors.
   */
  val favouriteSensorsPicking: LiveData<Pair<Sensors, Sensors>>
  val preferences = mapPreferencesStorage.preferences
  val mapPolygons: LiveData<List<PolygonOptions>>
  val  mapPolygonsSensorReadingDaysRange: LiveData<List<PolygonOptions>>
  val bottomSheetPeek: LiveData<BottomSheetPeekViewModel>

  private val _isSpecificSensorSelected = MutableLiveData<Boolean>()
  val isSpecificSensorSelected: LiveData<Boolean>
    get() = _isSpecificSensorSelected

  var averageWeeklyData: LiveData<AverageWeeklyDataModel>
  var averageDataMonthDays: LiveData<AverageWeeklyDataModel>
  var averageDataGivenRange: LiveData<AverageWeeklyDataModel>
  var averageMonthDataByYear: LiveData<AverageWeeklyDataModel>


  init {
    val dataDefinitionData = Transformations.switchMap(selectedMeasurementType) {
      dataDefinitionProvider[it]
    }
    dataDefinitionDataPublicHelper = dataDefinitionData

    val currentReadings = Transformations.switchMap(dataDefinitionData) { dataDefinition ->
      Transformations.map(cityPulseRepository.currentReadings) { current ->
        Pair(dataDefinition,
          current.data.orEmpty().mapNotNull { currentSensorReading ->
            currentSensorReading.readings[dataDefinition.id]?.let { currentSensorReading.sensor to it }
          })
      }
    }

    val sensorReadingsDaysRange = Transformations.switchMap(dataDefinitionData) { dataDefinition ->
      Transformations.map(cityPulseRepository.sensorReadings) { current ->
        Pair(dataDefinition,
          current.data.orEmpty().mapNotNull { currentSensorReading ->
            currentSensorReading.readings[dataDefinition.id]?.let { currentSensorReading.sensor to it }
          })
      }
    }
    val calculatedMapMarkersSensorReadingsDaysRange = Transformations.map(sensorReadingsDaysRange) { (dataDefinition, measurements) ->
        measurements.map { (sensor, reading) ->
          val markerColor = dataDefinition.findBandByValue(reading.value).markerColor
          MapMarkerModel(sensor, reading.value.roundToInt(), markerColor)
        }
      }

    val calculatedMapMarkers = Transformations.map(currentReadings) { (dataDefinition, measurements) ->
        measurements.map { (sensor, reading) ->
          val markerColor = dataDefinition.findBandByValue(reading.value).markerColor
          MapMarkerModel(sensor, reading.value.roundToInt(), markerColor)
        }
      }

    mapMarkersSensorReadingDaysRange = Transformations.distinctUntilChanged(
      preferences.filter(calculatedMapMarkersSensorReadingsDaysRange, MutableLiveData(emptyList())) {
        it.dataVisualization.contains(MARKERS)
      }
    )

    mapMarkers = Transformations.distinctUntilChanged(
      preferences.filter(calculatedMapMarkers, MutableLiveData(emptyList())) {
        it.dataVisualization.contains(MARKERS)
      }
    )


    overallBannerData = Transformations.map(currentReadings) { (dataDefinition, measurements) ->
      measurements.map { (_, reading) -> reading.value }.average().takeUnless { it.isNaN() }
        ?.roundToInt()?.let {
        val valueBand = dataDefinition.findBandByValue(it)
        OverallBannerData(
          context.getString(string.average),
          it.toString(),
          dataDefinition.unit,
          valueBand.grade,
          valueBand.legendColor,
          calculateLegend(dataDefinition, it)
        )
      }
    }

    val sensorOverview = Transformations.switchMap(selectedSensor) { selectedSensor ->
      Transformations.switchMap(favouriteSensors) { favouriteSensors ->
        Transformations.map(currentReadings) { (dataDefinition, sensorReadings) ->
          sensorReadings.firstOrNull { selectedSensor == it.first }?.let { (sensor, sensorReading) ->
            SensorOverviewModel(
                sensor,
                sensorReading.value,
                dataDefinition.unit,
                sensorReading.stamp,
                favouriteSensors.contains(sensor))
          }
        }
      }
    }

    bottomSheetPeek = Transformations.switchMap(sensorOverview) { sensorOverviewModel ->
      return@switchMap Transformations.map(favouriteSensors) { favouriteSensors ->
        BottomSheetPeekViewModel(
            sensorOverviewModel,
            favouriteSensors.isNotEmpty(),
            favouriteSensors.count() < 5)
      }
    }

    val historicalSensorReadings = Transformations.switchMap(dataDefinitionData) { dataDefinition ->
      Transformations.map(cityPulseRepository.historicalReadings) { sensorReadings ->
        dataDefinition to (sensorReadings.data?.map {
          (it.sensor to (it.readings[dataDefinition.id] ?: emptyList()))
        }?.toMap() ?: emptyMap())
      }
    }

    val measurementsForFavouriteSensors = Transformations.switchMap(
        favouriteSensors) { favouriteSensors ->
      Transformations.map(historicalSensorReadings) { (dataDefinition, sensorReadings) ->
        return@map Pair(dataDefinition,
            sensorReadings.filter { (sensor, _) -> favouriteSensors.contains(sensor) })
      }
    }

    graphData = Transformations.switchMap(selectedSensor) { selectedSensor ->
      if (selectedSensor == null) {
        return@switchMap Transformations.map(
            measurementsForFavouriteSensors) { (dataDefinition, data) ->
          createGraphModel(dataDefinition, data)
        }
      } else {
        return@switchMap Transformations.map(historicalSensorReadings) { (dataDefinition, data) ->
          return@map createGraphModel(dataDefinition, data.filter { it.key == selectedSensor })
        }
      }
    }

    averageWeeklyData = Transformations.switchMap(selectedMeasurementType) { measurementType ->
      Transformations.switchMap(selectedSensor) { sensor ->
        val averageLiveData = cityPulseRepository.getAverageWeeklyData(sensor?.id, measurementType)
        _isSpecificSensorSelected.value = sensor == null
        Transformations.map(averageLiveData) { responseData ->
          responseData.data?.let { readings ->
            AverageWeeklyDataModel(readings)
          }
        }
      }
    }

    averageDataGivenRange = Transformations.switchMap(selectedMeasurementType) { measurementType ->
      Transformations.switchMap(selectedSensor) { sensor ->
        val averageLiveData = cityPulseRepository.getAverageDataGivenRange(sensor?.id, measurementType,MapFragment.fromDate,MapFragment.toDate)
        _isSpecificSensorSelected.value = sensor == null
        Transformations.map(averageLiveData) { responseData ->
          responseData.data?.let { readings ->
            AverageWeeklyDataModel(readings)
          }
        }
      }
    }



    averageDataMonthDays = Transformations.switchMap(selectedMeasurementType) { measurementType ->
      Transformations.switchMap(selectedSensor) { sensor ->
        val averageLiveData = cityPulseRepository.getAverageDataMonthDays(sensor?.id,measurementType,CalendarAdapter.DATE_INPUT_TODAY)
        _isSpecificSensorSelected.value = sensor == null
        Transformations.map(averageLiveData) { responseData ->
          responseData?.data?.let { readings ->
            AverageWeeklyDataModel(readings)
          }
        }
      }
    }

   averageMonthDataByYear = Transformations.switchMap(selectedMeasurementType) { measurementType ->
      Transformations.switchMap(selectedSensor) { sensor ->
        val averageLiveData = cityPulseRepository.getAverageMonthlyData(sensor?.id,measurementType,LocalDate.ofYearDay(CalendarAdapter.DATE_INPUT?.year?:LocalDate.now().year,1),LocalDate.ofYearDay((CalendarAdapter.DATE_INPUT?.year?:LocalDate.now().year)+1,1))
        _isSpecificSensorSelected.value = sensor == null
        Transformations.map(averageLiveData) { responseData ->
          responseData?.data?.let { readings ->
            AverageWeeklyDataModel(readings)
          }
        }
      }
    }

    showNoSensorsFavourited = Transformations.switchMap(selectedSensor) { selectedSensor ->
      if (selectedSensor == null) {
        return@switchMap Transformations.map(hasFavouriteSensors) { it.not() }
      } else {
        return@switchMap MutableLiveData<Boolean>().apply { value = false }
      }
    }

    favouriteSensorsPicking = favouriteSensors.combine(
        cityPulseRepository.sensors) { favourite, all ->
      Pair(favourite.toList(), all.data?.filter { !favourite.contains(it) } ?: emptyList())
    }

    val calculatedMapPolygons = Transformations.map(currentReadings) { (dataDefinition, measurements) ->
      val dataToVisualise = measurements.map { (sensor, sensorReadings) ->
        val measurement = sensorReadings.value.roundToInt()
        sensor to measurement
      }
      val sensorPoints = dataToVisualise.map { (sensor, _) ->
        PointD(sensor.position!!.latitude, sensor.position.longitude)
      }
      if (sensorPoints.isEmpty()) {
        return@map emptyList<PolygonOptions>()
      }
      val voronoiPoints = sensorPoints.union(
          city.cityBorderPoints.map { PointD(it.latitude, it.longitude) })
      val voronoiResult = Voronoi.findAll(voronoiPoints.toTypedArray(), city.voronoiCityBounds)

      // Map the polygon to the measurement by finding the sensor that is located inside the polygon
      val dataToDraw = dataToVisualise.map { (sensor, measurement) ->
        voronoiResult.voronoiRegions().first { region ->
          GeoUtils.pointInPolygon(PointD(sensor.position!!.latitude, sensor.position.longitude),
              region) == PolygonLocation.INSIDE
        } to measurement
      }

      return@map dataToDraw.map { (region, measurement) ->
        PolygonOptions().apply {
          addAll(region.map { LatLng(it.x, it.y) })
          strokeWidth(0f)
          fillColor(dataDefinition.interpolateColor(measurement).withAlpha(146))
        }
      }
    }

    val calculatedMapPolygonsSensorReadingDaysRange = Transformations.map(sensorReadingsDaysRange) { (dataDefinition, measurements) ->
      val dataToVisualise = measurements.map { (sensor, sensorReadings) ->
        val measurement = sensorReadings.value.roundToInt()
        sensor to measurement
      }
      val sensorPoints = dataToVisualise.map { (sensor, _) ->
        PointD(sensor.position!!.latitude, sensor.position.longitude)
      }
      if (sensorPoints.isEmpty()) {
        return@map emptyList<PolygonOptions>()
      }
      val voronoiPoints = sensorPoints.union(
        city.cityBorderPoints.map { PointD(it.latitude, it.longitude) })
      val voronoiResult = Voronoi.findAll(voronoiPoints.toTypedArray(), city.voronoiCityBounds)

      val dataToDraw = dataToVisualise.map { (sensor, measurement) ->
        voronoiResult.voronoiRegions().first { region ->
          GeoUtils.pointInPolygon(PointD(sensor.position!!.latitude, sensor.position.longitude),
            region) == PolygonLocation.INSIDE
        } to measurement
      }

      return@map dataToDraw.map { (region, measurement) ->
        PolygonOptions().apply {
          addAll(region.map { LatLng(it.x, it.y) })
          strokeWidth(0f)
          fillColor(dataDefinition.interpolateColor(measurement).withAlpha(146))
        }
      }
    }

    mapPolygons = Transformations.distinctUntilChanged(
        preferences.filter(calculatedMapPolygons, MutableLiveData(emptyList())) {
          it.dataVisualization.contains(VORONOI)
        })

    mapPolygonsSensorReadingDaysRange =  Transformations.distinctUntilChanged(
      preferences.filter(calculatedMapPolygonsSensorReadingDaysRange, MutableLiveData(emptyList())) {
        it.dataVisualization.contains(VORONOI)
      })


    loadingResources.addResource(cityPulseRepository.currentReadings.toLoadingLiveDataResource())
    loadingResources.addResource(cityPulseRepository.historicalReadings.toLoadingLiveDataResource())
    errorResources.addResource(cityPulseRepository.currentReadings.toErrorLiveDataResource())
    errorResources.addResource(cityPulseRepository.historicalReadings.toErrorLiveDataResource())
  }

  override fun refreshData(forceRefresh: Boolean) {
    super.refreshData(forceRefresh)
    cityPulseRepository.refreshCurrent(forceRefresh)
  }

  fun getSensorValues(selectedMeasurementType: MeasurementType?) :LiveData<Resource<List<SensorReading>>> {
    return cityPulseRepository.getSensorValue(selectedMeasurementType)
  }

  fun getAvgDataRangeGiven(sensorId:String?, selectedMeasurementType:MeasurementType?, fromDate: LocalDate, toDate: LocalDate): LiveData<Resource<List<SensorReading>>>{
    return cityPulseRepository.getAverageDataGivenRange(sensorId,selectedMeasurementType,fromDate,toDate)
  }

  fun getAvgDataMonthPreviuosMonth(sensorId:String?, selectedMeasurementType:MeasurementType?, fromDate: LocalDate): LiveData<Resource<List<SensorReading>>?> {
    return cityPulseRepository.getAverageDataMonthDays(sensorId, selectedMeasurementType, fromDate)
  }

  fun getAverageMonthlyData(sensorId:String?, selectedMeasurementType:MeasurementType?, fromDate: LocalDate, toDate: LocalDate) : LiveData<Resource<List<SensorReading>>>{
    return cityPulseRepository.getAverageMonthlyData(sensorId,selectedMeasurementType, fromDate, toDate)
  }

  /**
   * Request loading of historical data. Not part of [MapViewModel.refreshData] since this data is quite big and rarely needed.
   */
  fun loadHistoricalReadings(forceRefresh: Boolean = false) {
    cityPulseRepository.refreshData24(forceRefresh)
  }

  fun showDataForMeasurementType(measurementType: MeasurementType) {
    if (selectedMeasurementType.value != measurementType) selectedMeasurementType.value = measurementType
  }

  /**
   * Apply the provided [Sensor] to [MapViewModel.selectedSensor].
   */
  fun selectSensor(sensor: Sensor) {
    if (selectedSensor.value != sensor) {
      selectedSensor.value = sensor
    }
  }

  /**
   * Deselect [MapViewModel.selectedSensor] if there was such.
   */
  fun deselectSensor() {
    if (selectedSensor.value != null) {
      selectedSensor.value = null
    }
  }

  /**
   * Apply and persist the provided [Preferences].
   */
  fun updatePreference(newPreferences: Preferences) {
    mapPreferencesStorage.updatePreferences(newPreferences)
  }

  private fun createGraphModel(dataDefinition: DataDefinition,
      sensorReadings: Map<Sensor, SensorReadings>): GraphModel {
    val graphBands = dataDefinition.bands.map { band ->
      GraphBand(band.from, band.to, band.legendColor)
    }
    val colorIterator = PULSE_SENSOR_COLORS.iterator()
    val graphDataSet = sensorReadings.map { (sensor, sensorReadings) ->
      val measurementsForType = sensorReadings.filter { dataDefinition.id == it.type }
      return@map GraphSeries(
          measurementsForType.map { Pair(it.stamp.time, it.value) },
          sensor.description.toUpperCase(Locale.getDefault()),
          colorIterator.nextInt())
    }.filter { it.measurements.isNotEmpty() }
    return GraphModel(graphBands, graphDataSet)
  }

  fun createAverageOverallBannerData(sensorReading: SensorReading, dataDefinitionData: DataDefinition): OverallBannerData {
    val sensorValueInt = sensorReading.value.toInt()
    val valueBand = dataDefinitionData.findBandByValue(sensorValueInt)
    return OverallBannerData(
      context.getString(string.average),
      sensorValueInt.toString(),
      dataDefinitionData.unit,
      valueBand.grade,
      valueBand.legendColor,
      calculateLegend(dataDefinitionData, sensorValueInt))
  }

  private fun calculateLegend(dataDefinition: DataDefinition, value: Int): Legend {
    val legendMin = dataDefinition.legendMin
    val legendMax = dataDefinition.legendMax

    val currentLegendValue = (((value - legendMin).toFloat() / (legendMax - legendMin)) * 100).toInt()
    var prevBand: Band? = null
    val legendBands = dataDefinition.bands.map { band ->
      val endAt = ((min(band.to, legendMax) - legendMin).toDouble() / (legendMax - legendMin)) * 100
      val labelStart = "${min(max(prevBand?.to ?: band.from, legendMin),
          legendMax)}${dataDefinition.unit}"
      val labelEnd = "${max(min(band.to, legendMax), legendMin)}${dataDefinition.unit}"
      prevBand = band
      LegendBand(
          endAt.toInt(),
          band.legendColor,
          labelStart, labelEnd
      )
    }
    return Legend(currentLegendValue, legendBands)
  }


}