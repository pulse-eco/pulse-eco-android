package com.netcetera.skopjepulse.cityselect

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.CurrentLocationProvider
import com.netcetera.skopjepulse.LocationServicesDisabled
import com.netcetera.skopjepulse.MissingLocationPermission
import com.netcetera.skopjepulse.R.string
import com.netcetera.skopjepulse.base.Event
import com.netcetera.skopjepulse.base.PreferredCityStorage
import com.netcetera.skopjepulse.base.data.DataDefinitionProvider
import com.netcetera.skopjepulse.base.data.Resource
import com.netcetera.skopjepulse.base.data.Resource.Status.LOADING
import com.netcetera.skopjepulse.base.data.Resource.Status.SUCCESS
import com.netcetera.skopjepulse.base.data.repository.PulseRepository
import com.netcetera.skopjepulse.base.model.City
import com.netcetera.skopjepulse.base.model.MeasurementType
import com.netcetera.skopjepulse.base.viewModel.BaseViewModel
import com.netcetera.skopjepulse.base.viewModel.toErrorLiveDataResource
import com.netcetera.skopjepulse.base.viewModel.toLoadingLiveDataResource
import com.netcetera.skopjepulse.countryCitySelector.CityItem
import com.netcetera.skopjepulse.extensions.combine
import com.netcetera.skopjepulse.extensions.isInt
import java.util.*


/**
 * Implementation of [BaseViewModel] that is used for displaying of cities to select from in [CitySelectFragment].
 */
class CitySelectViewModel(
    private val pulseRepository: PulseRepository,
    private val locationProvider: CurrentLocationProvider,
    private val dataDefinitionProvider: DataDefinitionProvider) : BaseViewModel() {

  private val selectedMeasurementType = MutableLiveData<MeasurementType>()
  private val _requestLocationPermission = MutableLiveData<Event<Unit>>()

  private val sharedPref = context.getSharedPreferences(Constants.SELECTED_CITIES, Context.MODE_PRIVATE)

  /**
   * Emitting of [Event] when there is permission missing for access of the current user location.
   */
  val requestLocationPermission : LiveData<Event<Unit>>
    get() = _requestLocationPermission

  /**
   * The display models of the cities that once can choose from.
   */
  lateinit var citySelectItems: LiveData<List<CitySelectItem>>

  lateinit var allCityItems: LiveData<List<CitySelectItem>>

  private var _shouldRefreshSelectedCities = MutableLiveData<Boolean>()

  val shouldRefreshSelectedCities :LiveData<Boolean>
    get() = _shouldRefreshSelectedCities

  private val _citiesSharedPref = MutableLiveData<String>()

  val activeCity: LiveData<City?>
  private val selectableCity = MutableLiveData<City?>()
  private val cityStorage: PreferredCityStorage = PreferredCityStorage(context)


  init {
    loadData()
    getSelectedCities()
    _shouldRefreshSelectedCities.value = false

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
  }

  /**
   * Get selected cities in [citySelectItems]
   */
  fun getSelectedCities() {
    _citiesSharedPref.value = sharedPref.getString(Constants.SELECTED_CITIES, "")
    var selectedCitiesSet = HashSet<CityItem>()
    val gson = Gson()
    val selectedCities = _citiesSharedPref.value
    if (!selectedCities.isNullOrEmpty()) {
      val type = object : TypeToken<HashSet<CityItem>>() {}.type
      selectedCitiesSet = gson.fromJson(selectedCities, type)
    } else {
      // when loading for the first time
      selectedCitiesSet.add(CityItem("Skopje", "Macedonia"))
      val editor: SharedPreferences.Editor = sharedPref.edit()
      val jsonSelectedCities = gson.toJson(selectedCitiesSet)
      editor.putString(Constants.SELECTED_CITIES, jsonSelectedCities)
      editor.apply()
    }

    citySelectItems = Transformations.map(allCityItems) {
      it.filter {
        selectedCitiesSet.map { it.name.toLowerCase(Locale.ROOT) }.contains(it.city.name.toLowerCase(Locale.ROOT))
      }
    }
  }

  fun deleteCityOnSwipe(cityToRemove: String) {
    _citiesSharedPref.value = sharedPref.getString(Constants.SELECTED_CITIES, "")
    var selectedCitiesSet = HashSet<CityItem>()
    val gson = Gson()

    val selectedCities = _citiesSharedPref.value
    val type = object : TypeToken<HashSet<CityItem>>() {}.type
    selectedCitiesSet = gson.fromJson(selectedCities, type)

    for (c in selectedCitiesSet) {
      if (c.name.equals(cityToRemove, ignoreCase = true)) {
        if (c.name.equals(cityStorage.cityId, ignoreCase = true)) {
          cityStorage.cityId = ""
          selectableCity.value = null
        }
        selectedCitiesSet.remove(c)
        break
      }
    }


    val editor: SharedPreferences.Editor = sharedPref.edit()
    val jsonSelectedCities = gson.toJson(selectedCitiesSet)
    editor.putString(Constants.SELECTED_CITIES, jsonSelectedCities)
    editor.apply()

    getSelectedCities()
    _shouldRefreshSelectedCities.value = true
  }

  /**
   * Special error handling for the [CurrentLocationProvider.currentLocation].
   * @see BaseViewModel.handleError
   */
  override fun handleError(resource: Resource<Any>): String? {
    return when (resource.throwable) {
      is MissingLocationPermission -> {
        if (_requestLocationPermission.value?.hasBeenHandled != true) _requestLocationPermission.value = Event(Unit)
        context.getString(string.city_select_missing_location_permission_error_message)
      }
      is LocationServicesDisabled -> context.getString(
          string.city_select_location_services_disabled_error_message)
      else -> super.handleError(resource)
    }
  }

  /**
   * Request refresh of the data for [CitySelectViewModel.citySelectItems].
   * @see BaseViewModel.refreshData
   */
  override fun refreshData(forceRefresh: Boolean) {
    if (pulseRepository.cities.value?.data.isNullOrEmpty()) {
      pulseRepository.loadCities(true)
    } else {
      pulseRepository.loadCitiesOverall()
    }
  }

  /**
   * Request location update after failure via [CitySelectViewModel.requestLocationPermission].
   */
  fun requestLocation() {
    locationProvider.requestLocation()
  }

  fun showDataForMeasurementType(measurementType: MeasurementType) {
    if (measurementType != selectedMeasurementType.value) selectedMeasurementType.value = measurementType
  }


  fun loadData(){
    val sortedCities = Transformations.switchMap(pulseRepository.cities) { cities ->
      Transformations.map(locationProvider.currentLocation) { location ->
        val locationToSortBy = when(location.status) {
          SUCCESS, LOADING -> location.data
          else -> null
        }
        if (locationToSortBy == null) {
          cities.data?.sortedWith(City.macedoniaFirstComparator())
        } else {
          cities.data?.sortedBy { city -> city.location.distanceTo(location.data) }
        } ?: emptyList()
      }

    }

    val dataDefinitionData = Transformations.switchMap(selectedMeasurementType) {
      dataDefinitionProvider[it]
    }

    allCityItems = Transformations.switchMap(dataDefinitionData) { dataDefinition ->
      sortedCities.combine(pulseRepository.citiesOverall) { cities, overalls ->
        return@combine cities.mapNotNull { city ->
          val cityOverall = overalls.data?.firstOrNull { it.cityName == city.name }
          val measurement = cityOverall?.values?.get(dataDefinition.id)

          when {
            measurement?.isInt() == true -> {
              val measurementBand = dataDefinition.findBandByValue(measurement.toInt())
              CitySelectItem(
                city,
                measurementBand.grade,
                measurement.toInt().toString(),
                dataDefinition.unit,
                measurementBand.legendColor
              )
            }
            else -> CitySelectItem(
              city,
              dataDefinition.description,
              "N/A",
              dataDefinition.unit,
              Color.LTGRAY
            )
          }
        }
      }
    }

    loadingResources.addResource(pulseRepository.citiesOverall.toLoadingLiveDataResource())
    errorResources.addResource(pulseRepository.citiesOverall.toErrorLiveDataResource())
    errorResources.addResource(locationProvider.currentLocation.toErrorLiveDataResource())
  }

}
