package com.netcetera.skopjepulse.base.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.netcetera.skopjepulse.base.data.repository.CityPulseRepository
import com.netcetera.skopjepulse.base.data.repository.FavouriteSensorsStorage
import com.netcetera.skopjepulse.base.model.Sensor

/**
 * Abstract implementation of [BaseViewModel] that manages favourite sensor data.
 * Once upon a time this had purpose, but now only makes MapViewModel a bit shorter.
 */
abstract class PulseViewModel(
    private val pulseCityPulseRepository: CityPulseRepository,
    private val favouriteSensorsStorage: FavouriteSensorsStorage) : BaseViewModel() {

  val favouriteSensors: LiveData<Set<Sensor>>
  val hasFavouriteSensors: LiveData<Boolean>

  init {

    val sensorsResource = pulseCityPulseRepository.sensors

    favouriteSensors = Transformations.switchMap(favouriteSensorsStorage.favouriteSensors) { favouriteSensorsIds ->
      Transformations.map(sensorsResource) { sensorsResource ->
        sensorsResource.data?.filter { favouriteSensorsIds.contains(it.id) }?.toSet() ?: emptySet()
      }
    }
    hasFavouriteSensors = Transformations.map(favouriteSensors) { it.isNotEmpty() }

    errorResources.addResource(sensorsResource.toLoadingLiveDataResource())
    loadingResources.addResource(sensorsResource.toErrorLiveDataResource())
  }

  override fun refreshData(forceRefresh: Boolean) {
    pulseCityPulseRepository.refreshSensors(forceRefresh)
  }

  fun favouriteSensor(sensor: Sensor) {
    favouriteSensorsStorage.addSensorAsFavourite(sensor)
  }

  fun unFavouriteSensor(sensor: Sensor) {
    favouriteSensorsStorage.removeSensorFromFavourite(sensor)
  }
}