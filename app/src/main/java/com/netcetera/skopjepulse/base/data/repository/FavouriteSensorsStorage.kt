package com.netcetera.skopjepulse.base.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netcetera.skopjepulse.base.model.City
import com.netcetera.skopjepulse.base.model.Sensor

private const val SELECTED_SENSORS_KEY = "selected_sensors"

class FavouriteSensorsStorage(context: Context, city: City) {
  private val sharedPreferences = context.getSharedPreferences("favourite_sensors_${city.name}", Context.MODE_PRIVATE)

  private var favoriteSensorsStorage: Set<String>
    get() = sharedPreferences.getStringSet(SELECTED_SENSORS_KEY, emptySet()) as Set<String>
    private set(value) {
      sharedPreferences.edit().putStringSet(SELECTED_SENSORS_KEY, value).apply()
      _favouriteSensors.value = value
    }

  private val _favouriteSensors = MutableLiveData<Set<String>>().apply { value = favoriteSensorsStorage }
  val favouriteSensors: LiveData<Set<String>>
    get() = _favouriteSensors

  fun addSensorAsFavourite(sensor: Sensor): Boolean {
    val active = favoriteSensorsStorage.toMutableSet()
    if (!active.contains(sensor.id)) {
      favoriteSensorsStorage = active.apply { add(sensor.id) }
      return true
    }
    return false
  }

  fun removeSensorFromFavourite(sensor: Sensor): Boolean {
    val active = favoriteSensorsStorage.toMutableSet()
    if (active.contains(sensor.id)) {
      favoriteSensorsStorage = active.apply { remove(sensor.id) }
      return true
    }
    return false
  }

  fun isSensorFavourite(sensor: Sensor): Boolean = favoriteSensorsStorage.contains(sensor.id)
}