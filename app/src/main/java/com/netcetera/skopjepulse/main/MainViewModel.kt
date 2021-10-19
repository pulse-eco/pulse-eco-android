package com.netcetera.skopjepulse.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.netcetera.skopjepulse.base.PreferredCityStorage
import com.netcetera.skopjepulse.base.data.DataDefinitionProvider
import com.netcetera.skopjepulse.base.data.Resource
import com.netcetera.skopjepulse.base.data.repository.PulseRepository
import com.netcetera.skopjepulse.base.model.City
import com.netcetera.skopjepulse.base.model.MeasurementType
import com.netcetera.skopjepulse.base.viewModel.BaseViewModel
import com.netcetera.skopjepulse.base.viewModel.toErrorLiveDataResource
import com.netcetera.skopjepulse.base.viewModel.toLoadingLiveDataResource
import com.netcetera.skopjepulse.pulseappbar.MeasurementTypeTab

/**
 * The main [androidx.lifecycle.ViewModel] used in [MainActivity] and shared across fragments.
 * Responsible for manging on what [City] and [MeasurementType] are shown in the fragments.
 */
class MainViewModel(
  private val pulseRepository: PulseRepository,
  cityStorage: PreferredCityStorage,
  private val dataDefinitionProvider: DataDefinitionProvider
) : BaseViewModel() {

  /**
   * The [City] that data shall be shown for.
   */
  val activeCity: LiveData<City?>
  private val selectableCity = MutableLiveData<City?>()

  private val selectableMeasurementType: MutableLiveData<MeasurementType>

  /**
   * The [MeasurementType] that data shall be shown for.
   */
  val activeMeasurementType: LiveData<MeasurementType>
    get() = selectableMeasurementType

  /**
   * The view model data for showing of possible [MeasurementType]s.
   */
  val measurementTypeTabs: LiveData<List<MeasurementTypeTab>>

  init {
    activeCity = Transformations.distinctUntilChanged(
      MediatorLiveData<City>().apply {
        addSource(pulseRepository.cities) { cities ->
          value = cities.data?.firstOrNull { it.name == cityStorage.cityId }
        }

        addSource(selectableCity) {
          value = it
          cityStorage.cityId = it?.name ?: ""

        }

      })
    selectableMeasurementType = MediatorLiveData<MeasurementType>().apply {
      addSource(dataDefinitionProvider.definitions) {
        // Workaround to make selection from the available types if nothing is selected
        val firstAvailable = it.firstOrNull()?.id
        if (value == null && firstAvailable != null) {
          removeSource(dataDefinitionProvider.definitions)
          value = firstAvailable
        }
      }
    }

    measurementTypeTabs = Transformations.distinctUntilChanged(
      Transformations.map(dataDefinitionProvider.definitions) { definitions ->
        definitions.map {
          MeasurementTypeTab(it.id, it.shortTitle)
        }
      })

    loadingResources.addResource(pulseRepository.cities.toLoadingLiveDataResource {
      if (!it.data.isNullOrEmpty()) Resource.idle() else null
    })
    errorResources.addResource(pulseRepository.cities.toErrorLiveDataResource())
  }

  /**
   * Reloads the [DataDefinitionProvider] data, so that changed configuration takes effect (ex. Language change)
   */
  fun reloadDDPData() {
    dataDefinitionProvider.loadData()
  }

  /**
   * Set the provided [MeasurementType] as [MainViewModel.activeMeasurementType].
   */
  fun showForMeasurement(measurementType: MeasurementType) {
    if (activeMeasurementType.value != measurementType) {
      selectableMeasurementType.value = measurementType
    }
  }

  /**
   * Set the provided [City] as [MainViewModel.activeCity]
   */
  fun showForCity(city: City?) {
    if (activeCity.value != city) {
      selectableCity.value = city
    }
  }

  override fun refreshData(forceRefresh: Boolean) {
    pulseRepository.loadCities(forceRefresh)
  }

  fun showForCity(city: String) {
    val foundCity =
      pulseRepository.cities.value?.data?.firstOrNull { it.name.equals(city, ignoreCase = true) }
    if (foundCity != null) {
      showForCity(foundCity)
    }
  }
}