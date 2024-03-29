package com.netcetera.skopjepulse.base.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.Constants.Companion.DAY_IN_MILLISECONDS
import com.netcetera.skopjepulse.base.data.Resource
import com.netcetera.skopjepulse.base.data.api.CityPulseApiService
import com.netcetera.skopjepulse.base.model.MeasurementType
import com.netcetera.skopjepulse.base.model.Sensor
import com.netcetera.skopjepulse.base.model.SensorReading
import com.netcetera.skopjepulse.extensions.resourceCombine
import com.netcetera.skopjepulse.historyforecast.HistoryForecastAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

/**
 * Repository that is responsible for loading data for a single city from the pulse.eco platform.
 * @param apiService the interface towards the pulse.eco REST API.
 */
class CityPulseRepository(private val apiService : CityPulseApiService) : BasePulseRepository() {

  companion object {
    var fullDateFormatter = SimpleDateFormat(Constants.FULL_DATE_FORMAT, Locale.getDefault())
  }

  private val _sensors = MutableLiveData<Resource<Sensors>>()
  private val _sensorReading = MutableLiveData<Resource<SensorReadings>>()
  private val _current = MutableLiveData<Resource<SensorReadings>>()
  private val _data24 = MutableLiveData<Resource<SensorReadings>>()

  val sensors: LiveData<Resource<Sensors>>
    get() = _sensors

  val currentReadings : LiveData<Resource<List<CurrentSensorReading>>>
  val historicalReadings : LiveData<Resource<List<HistoricalSensorReadings>>>
  val sensorReadings : LiveData<Resource<List<CurrentSensorReading>>>

  init {
    currentReadings = sensors.resourceCombine(_current) { sensors, readings ->
      sensors.map { sensor ->
        CurrentSensorReading(
            sensor,
            readings
                .filter { reading -> sensor.id == reading.sensorId }
                .map { reading -> reading.type to reading }
                .toMap())
      }
    }

    sensorReadings = sensors.resourceCombine(_sensorReading) { sensors, readings ->
      sensors.map { sensor ->
        //generates the average value from N SensorReadings per sensor in a given day
        val averageFromReadings = readings
          .filter { singleReading ->
            sensor.id == singleReading.sensorId }
          .map { singleReading ->
            singleReading.value }.average()
        val mappedReading = readings
          .filter { singleReading ->
            sensor.id == singleReading.sensorId
          }
          .associateBy { singleReading ->
            singleReading.type
          }
        //sets the average for all SensorReadings in a given day day
        mappedReading.values.forEach { it.value = averageFromReadings }
        CurrentSensorReading (
          sensor,
          mappedReading
        )
      }
    }


    historicalReadings = sensors.resourceCombine(_data24) { sensors, readings ->
      sensors.map { sensor ->
        val sensorReadings = readings.filter { it.sensorId == sensor.id }
        HistoricalSensorReadings(
            sensor,
            sensorReadings.groupBy { it.type }
        )
      }
    }
  }

  /**
   * Request refresh of the [CityPulseRepository.sensors] resource.
   */
  fun refreshSensors(forceRefresh: Boolean) {
    if (!shouldRefresh(sensors, forceRefresh)) {
      return
    }
    _sensors.value = Resource.loading(sensors.value?.data)
    apiService.getSensors().enqueue(object : Callback<Sensors> {

      override fun onResponse(call: Call<Sensors>, response: Response<Sensors>) {
        if (response.isSuccessful && response.body() != null) {
          _sensors.value = Resource.success(response.body()!!)
          refreshStamps[sensors] = Date().time
        } else {
          _sensors.value = Resource.error(sensors.value?.data, null)
          refreshStamps.remove(sensors)
        }
      }

      override fun onFailure(call: Call<Sensors>, t: Throwable) {
        _sensors.value = Resource.error(sensors.value?.data, t)
        refreshStamps.remove(sensors)
      }
    })
  }

  fun refreshCurrent(forceRefresh: Boolean) {
    if (!shouldRefresh(_current, forceRefresh)) return

    _current.value = Resource.loading(_current.value?.data)
    apiService.getCurrent().enqueue(object : Callback<SensorReadings> {
      override fun onResponse(call: Call<SensorReadings>, response: Response<SensorReadings>) {
        if (response.isSuccessful && response.body() != null) {
          _current.value = Resource.success(response.body()!!)
          refreshStamps[_current] = Date().time
        } else {
          _current.value = Resource.error(_current.value?.data, null)
          refreshStamps.remove(_current)
        }
      }

      override fun onFailure(call: Call<SensorReadings>, t: Throwable) {
        _current.value = Resource.error(_current.value?.data, t)
        refreshStamps.remove(_current)
      }
    })
  }

  /**
   * Request refresh of the [CityPulseRepository.data24] resource.
   */
  fun refreshData24(forceRefresh: Boolean) {
    if (!shouldRefresh(_data24, forceRefresh)) {
      return
    }
    _data24.value = Resource.loading(_data24.value?.data)
    apiService.getData24h().enqueue(object : Callback<SensorReadings> {
      override fun onFailure(call: Call<SensorReadings>, t: Throwable) {
        _data24.value = Resource.error(_data24.value?.data, t)
        refreshStamps.remove(_data24)
      }

      override fun onResponse(call: Call<SensorReadings>, response: Response<SensorReadings>) {
        if (response.isSuccessful && response.body() != null) {
          _data24.value = Resource.success(response.body()!!)
          refreshStamps[_data24] = Date().time
        } else {
          _data24.value = Resource.error(_data24.value?.data, null)
          refreshStamps.remove(_data24)
        }
      }
    })
  }

  fun getSensorsValuesFromType(selectedMeasurementType: MeasurementType?): LiveData<Resource<List<SensorReading>>> {
    val result = MutableLiveData<Resource<List<SensorReading>>> ()
    val mutableList = mutableListOf<SensorReading>()
    val formatter = SimpleDateFormat(Constants.FULL_DATE_FORMAT)
    val fromDate = formatter.format(HistoryForecastAdapter.TIME_STAMP)
    val localFromDate = ZonedDateTime.parse(fromDate).withZoneSameInstant(ZoneId.systemDefault())
    val toDateCalculation = HistoryForecastAdapter.TIME_STAMP.time + DAY_IN_MILLISECONDS
    val toDate = formatter.format(toDateCalculation)
    val localToDate = ZonedDateTime.parse(toDate).withZoneSameInstant(ZoneId.systemDefault())
    val checkFormatter = SimpleDateFormat("yyyy-MM-dd")

    apiService.getAllSensorsAllValuesForTypeInRange(selectedMeasurementType!!,localFromDate.toString(), localToDate.toString()).enqueue(object : Callback<List<SensorReading>> {
        override fun onFailure(call: Call<List<SensorReading>>, t: Throwable) {
          result.postValue(Resource.error(null, t))
        }

        override fun onResponse(call: Call<List<SensorReading>>, response: Response<List<SensorReading>>) {
          for (i in 0 until response.body()!!.size) {
            if (checkFormatter.format(response.body()!![i].stamp) == checkFormatter.format(HistoryForecastAdapter.TIME_STAMP)) {
              mutableList += response.body()!![i]
            }
          }
          result.postValue(Resource.success(mutableList))
          _sensorReading.value = Resource.success(mutableList)
        }
      })
    return result
  }

  fun getAverageWeeklyData(
    sensorId: String?,
    selectedMeasurementType: MeasurementType?
  ): LiveData<Resource<List<SensorReading>>> {
    val result = MutableLiveData<Resource<List<SensorReading>>>()
    val id = sensorId ?: Constants.SENSOR_ID_FOR_AVERAGE_WEEKLY_DATA_FOR_WHOLE_CITY
    val cal: Calendar = Calendar.getInstance()
    val cal2: Calendar = Calendar.getInstance()
    cal2.add(Calendar.DATE, -8)
    val toDate = fullDateFormatter.format(cal.time)
    val date = cal2.time
    val fromDate: String = fullDateFormatter.format(date)

    apiService.getAvgDailyData(id, selectedMeasurementType!!, fromDate, toDate)
      .enqueue(object : Callback<List<SensorReading>> {
        override fun onFailure(call: Call<List<SensorReading>>, t: Throwable) {
          result.postValue(Resource.error(null, t))
        }

        override fun onResponse(
          call: Call<List<SensorReading>>,
          response: Response<List<SensorReading>>
        ) {
          if (response.isSuccessful && response.body() != null) {
            result.postValue(Resource.success(response.body()!!))
          }
        }
      })
    return result
  }

  fun getAverageMonthlyData(sensorId: String?, selectedMeasurementType: MeasurementType?, fromDate: LocalDate, toDate: LocalDate): LiveData<Resource<List<SensorReading>>>{
    val result = MutableLiveData<Resource<List<SensorReading>>>()
    val id = sensorId ?: Constants.SENSOR_ID_FOR_AVERAGE_WEEKLY_DATA_FOR_WHOLE_CITY
    val systemTimeZone: ZoneId = ZoneId.systemDefault()

    val fromDateZonedDateTime = fromDate.atStartOfDay(systemTimeZone)
    val dateFormatFromDate = Date.from(fromDateZonedDateTime.toInstant())
    val fromDateString = fullDateFormatter.format(dateFormatFromDate)

    val toDateZonedDateTime = toDate.atStartOfDay(systemTimeZone)
    val dateFormatToDate = Date.from(toDateZonedDateTime.toInstant())
    val toDateString = fullDateFormatter.format(dateFormatToDate)

    apiService.getAvgMonthData(id, selectedMeasurementType!!,fromDateString,toDateString).enqueue(object : Callback<List<SensorReading>> {

      override fun onFailure(call: Call<List<SensorReading>>, t: Throwable) {
        result.postValue(Resource.error(null, t))
      }

      override fun onResponse(call: Call<List<SensorReading>>, response: Response<List<SensorReading>>) {
        if (response.isSuccessful && response.body() != null) {
          result.postValue(Resource.success(response.body()!!))
        }
      }
    })
    return result
  }

  fun getAverageDataGivenRange(sensorId:String?,selectedMeasurementType:MeasurementType?,fromDate:LocalDate, toDate: LocalDate) : LiveData<Resource<List<SensorReading>>> {
    val result = MutableLiveData<Resource<List<SensorReading>>>()
    val id = sensorId ?: Constants.SENSOR_ID_FOR_AVERAGE_WEEKLY_DATA_FOR_WHOLE_CITY
    val systemTimeZone: ZoneId = ZoneId.systemDefault()

    val fromDateZonedDateTime = fromDate.atStartOfDay(systemTimeZone)
    val dateFormatFromDate = Date.from(fromDateZonedDateTime.toInstant())
    val fromDateString = fullDateFormatter.format(dateFormatFromDate)

    val toDateZonedDateTime = toDate.atStartOfDay(systemTimeZone)
    val dateFormatToDate = Date.from(toDateZonedDateTime.toInstant())
    val toDateString = fullDateFormatter.format(dateFormatToDate)

    apiService.getAvgDailyData(id, selectedMeasurementType!!, fromDateString, toDateString)
      .enqueue(object : Callback<List<SensorReading>> {

        override fun onFailure(call: Call<List<SensorReading>>, t: Throwable) {
          result.postValue(Resource.error(null, t))
        }

        override fun onResponse(
          call: Call<List<SensorReading>>,
          response: Response<List<SensorReading>>
        ) {
          if (response.isSuccessful && response.body() != null) {
            result.postValue(Resource.success(response.body()!!))
          }
        }
      })
    return result
  }

  fun getAverageDataMonthDays(sensorId: String?, selectedMeasurementType: MeasurementType?, fromDate: LocalDate): LiveData<Resource<List<SensorReading>>> {
    val result = MutableLiveData<Resource<List<SensorReading>>>()
    val id = sensorId ?: Constants.SENSOR_ID_FOR_AVERAGE_WEEKLY_DATA_FOR_WHOLE_CITY
    val fromDateLen = fromDate.lengthOfMonth() - 1
    val toDate = fromDate.plusDays(fromDateLen.toLong() + 1)
    val systemTimeZone: ZoneId = ZoneId.systemDefault()

    val dateFrom = fromDate.atStartOfDay(systemTimeZone)
    val utilDateFrom = Date.from(dateFrom.toInstant())

    val dateTo = toDate.atStartOfDay(systemTimeZone)
    val utilDateTo = Date.from(dateTo.toInstant())

    apiService.getAvgDailyData(id, selectedMeasurementType!!, fullDateFormatter.format(utilDateFrom), fullDateFormatter.format(utilDateTo)).enqueue(object : Callback<List<SensorReading>> {
      override fun onFailure(call: Call<List<SensorReading>>, t: Throwable) {
        result.postValue(Resource.error(null, t))
      }

      override fun onResponse(call: Call<List<SensorReading>>, response: Response<List<SensorReading>>) {
        if (response.isSuccessful && response.body() != null) {
          result.postValue(Resource.success(response.body()!!))
        }
      }
    })
    return result
  }

}

data class CurrentSensorReading(val sensor: Sensor, val readings : Map<MeasurementType, SensorReading>)
data class HistoricalSensorReadings(val sensor: Sensor, val readings: Map<MeasurementType, List<SensorReading>>)