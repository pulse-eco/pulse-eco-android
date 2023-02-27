package com.netcetera.skopjepulse.base.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.netcetera.skopjepulse.base.data.Resource
import com.netcetera.skopjepulse.base.data.api.PulseApiService
import com.netcetera.skopjepulse.base.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

typealias Cities = List<City>
typealias Sensors = List<Sensor>
typealias SensorReadings = List<SensorReading>

/**
 * Repository that is responsible for loading data for a for the  pulse.eco platform.
 * @param apiService the interface towards the pulse.eco REST API.
 */
open class PulseRepository(var apiService: PulseApiService) : BasePulseRepository() {
  private val _cities = MutableLiveData<Resource<Cities>>()
  private val _citiesOverall = MutableLiveData<Resource<List<CityOverall>>>()
  private val _cityOverall = MutableLiveData<Resource<List<CityOverall>>>()
  private val _countries = MutableLiveData<Resource<List<Country>>>()

  val cities: LiveData<Resource<Cities>>
    get() = _cities
  val citiesOverall: LiveData<Resource<List<CityOverall>>>
  val countries: LiveData<Resource<List<Country>>>
    get() = _countries

  init {
    citiesOverall = Transformations.switchMap(cities) { cities ->
      when (cities.status) {
        Resource.Status.SUCCESS -> {
          loadCitiesOverall(cities.data)
          _citiesOverall
        }
        Resource.Status.ERROR -> {
          _citiesOverall.apply {
            value = Resource.error(_citiesOverall.value?.data, cities.throwable)
          }
        }
        else -> {
          _citiesOverall.apply {
            value = Resource.loading(_citiesOverall.value?.data)
          }
        }
      }
    }

    apiService.countries().enqueue(object : Callback<List<Country>?> {
      override fun onResponse(call: Call<List<Country>?>, response: Response<List<Country>?>) {
        if (response.isSuccessful && response.body() != null) {
          _countries.value = Resource.success(response.body()!!)
          refreshStamps[countries] = Date().time
        } else {
          _countries.value = Resource.error(countries.value?.data, null)
          refreshStamps.remove(countries)
        }
      }

      override fun onFailure(call: Call<List<Country>?>, t: Throwable) {
        _countries.value = Resource.error(countries.value?.data, t)
        refreshStamps.remove(countries)
      }
    })
  }

  /**
   * Request update of the [PulseRepository.cities] resource.
   */
  fun loadCities(forceRefresh: Boolean) {
    if (!shouldRefresh(cities, forceRefresh)) {
      return
    }

    _cities.value = Resource.loading(cities.value?.data)
    apiService.cities().enqueue(object : Callback<List<City>?> {
      override fun onResponse(call: Call<List<City>?>, response: Response<List<City>?>) {
        if (response.isSuccessful && response.body() != null) {
          _cities.value = Resource.success(response.body()!!)
          refreshStamps[cities] = Date().time
        } else {
          _cities.value = Resource.error(cities.value?.data, null)
          refreshStamps.remove(cities)
        }
      }

      override fun onFailure(call: Call<List<City>?>, t: Throwable) {
        _cities.value = Resource.error(cities.value?.data, t)
        refreshStamps.remove(cities)
      }
    })
  }

  fun loadCitiesOverall(cities: List<City>? = this.cities.value?.data) {
    _citiesOverall.value = Resource.loading(citiesOverall.value?.data)
    apiService.getOverall(cities!!.map { it.name })
      .enqueue(object : Callback<List<CityOverall>?> {
        override fun onResponse(
          call: Call<List<CityOverall>?>,
          response: Response<List<CityOverall>?>
        ) {
          if (response.isSuccessful && response.body() != null) {
            _citiesOverall.value = Resource.success(response.body()!!)
            refreshStamps[citiesOverall] = Date().time
          } else {
            _citiesOverall.value = Resource.error(citiesOverall.value?.data, null)
          }
        }

        override fun onFailure(call: Call<List<CityOverall>?>, t: Throwable) {
          _citiesOverall.value = Resource.error(citiesOverall.value?.data, t)
        }
      })
  }

  fun getOverallData(cities: List<String>): LiveData<Resource<List<CityOverall>>> {
    _cityOverall.value = Resource.loading(_cityOverall.value?.data)
    apiService.getOverall(cities).enqueue(object : Callback<List<CityOverall>?> {
      override fun onResponse(
        call: Call<List<CityOverall>?>,
        response: Response<List<CityOverall>?>
      ) {
        if (response.isSuccessful && response.body() != null) {
          _cityOverall.value = Resource.success(response.body()!!)
        } else {
          _cityOverall.value = Resource.error(_cityOverall.value?.data, null)
        }
      }

      override fun onFailure(call: Call<List<CityOverall>?>, t: Throwable) {
        _cityOverall.value = Resource.error(_cityOverall.value?.data, t)
      }
    })
    return _cityOverall
  }
}