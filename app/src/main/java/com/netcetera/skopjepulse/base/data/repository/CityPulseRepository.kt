package com.netcetera.skopjepulse.base.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netcetera.skopjepulse.base.data.Resource
import com.netcetera.skopjepulse.base.data.api.CityPulseApiService
import com.netcetera.skopjepulse.base.model.MeasurementType
import com.netcetera.skopjepulse.base.model.Sensor
import com.netcetera.skopjepulse.base.model.SensorReading
import com.netcetera.skopjepulse.extensions.resourceCombine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

/**
 * Repository that is responsible for loading data for a single city from the pulse.eco platform.
 * @param apiService the interface towards the pulse.eco REST API.
 */
class CityPulseRepository(private val apiService : CityPulseApiService) : BasePulseRepository() {

  private val _sensors = MutableLiveData<Resource<Sensors>>()
  private val _current = MutableLiveData<Resource<SensorReadings>>()
  private val _data24 = MutableLiveData<Resource<SensorReadings>>()

  val sensors: LiveData<Resource<Sensors>>
    get() = _sensors
  val currentReadings : LiveData<Resource<List<CurrentSensorReading>>>
  val historicalReadings : LiveData<Resource<List<HistoricalSensorReadings>>>

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

}

data class CurrentSensorReading(val sensor: Sensor, val readings : Map<MeasurementType, SensorReading>)
data class HistoricalSensorReadings(val sensor: Sensor, val readings: Map<MeasurementType, List<SensorReading>>)
