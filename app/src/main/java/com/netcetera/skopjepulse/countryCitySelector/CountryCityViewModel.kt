package com.netcetera.skopjepulse.countryCitySelector

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.base.data.repository.PulseRepository
import com.netcetera.skopjepulse.extensions.transformData
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * Implementation of [androidx.lifecycle.ViewModel] for the [CountryCitySelectorActivity] that is used for
 * displaying of data of a Country/Cities.
 */

class CountryCityViewModel(application: Application, pulseRepository: PulseRepository) : AndroidViewModel(application) {
  private val _countryCityList = MutableLiveData<ArrayList<Any>>()
  val countryCityList: LiveData<ArrayList<Any>>
    get() = _countryCityList

  var checkedCityItems : HashSet<CityItem>
  val sharedPref = application.getSharedPreferences(Constants.SELECTED_CITIES, Context.MODE_PRIVATE)

  init {
    val data = ArrayList<CountryItem>()

    pulseRepository.countries.value?.data?.forEach{
      val country = it
      var cities = ArrayList<CityItem>()
      it.cities.forEach {
        cities.add(CityItem(it.name.capitalize(), country.countryName ))
      }
      data.add(CountryItem(it.countryName, it.countryCode, cities.toMutableList()))
    }

    checkedCityItems = HashSet()
    val gson = Gson()
    val selectedCities = sharedPref.getString(Constants.SELECTED_CITIES, "")
    val type = object: TypeToken<HashSet<CityItem>>() {}.type
    checkedCityItems = gson.fromJson(selectedCities, type)

    _countryCityList.value = data.transformData(checkedCityItems)
  }


  /**
   *  When the user clicks on one city, add it to sharedPreferences
   */
  fun saveCheckedCities(){
    val selectedCityItems = HashSet<CityItem>()
    val editor: SharedPreferences.Editor = sharedPref.edit()
    val gson = Gson()
    for (city in _countryCityList.value!!){
      if (city is CityItem && city.isChecked){
        selectedCityItems.add(city)
      }
    }
    val jsonSelectedCities = gson.toJson(selectedCityItems)
    editor.putString(Constants.SELECTED_CITIES, jsonSelectedCities)
    editor.apply()
  }

  /**
   * Returns the list of countries and cities that are currently not
   * present in the sharedPreferences
   */
  fun getSelectableCities(): List<Any>?{
    var checkableCities = countryCityList.value?.filter {
      !checkedCityItems.contains(it)
    }

    return checkableCities?.filterIndexed { index, it ->
      (it == checkableCities.last() && it !is CountryItem) ||
      (it != checkableCities.last() && !(it is CountryItem && checkableCities[index+1] is CountryItem))
    }
  }
}