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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * Implementation of [androidx.lifecycle.ViewModel] for the [CountryCitySelectorActivity] that is used for
 * displaying of data of a Country/Cities.
 */

class CountryCityViewModel(application: Application, pulseRepository: PulseRepository) : AndroidViewModel(application) {
  private val _countryCityList = MutableLiveData<ArrayList<CountryCityItem>>()
  val countryCityList: LiveData<ArrayList<CountryCityItem>>
    get() = _countryCityList

  var checkedCityItems : HashSet<CityItem>
  val sharedPref = application.getSharedPreferences(Constants.SELECTED_CITIES, Context.MODE_PRIVATE)

  init {
    val data = ArrayList<CountryItem>()

    pulseRepository.countries.value?.data?.forEach{
      val cities = ArrayList<CityItem>()
      it.cities.forEach {city ->
        cities.add(CityItem(city.name, it.countryName ))
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
  fun getSelectableCities(): List<CountryCityItem>?{
    val checkableCities = countryCityList.value?.filter {
      !checkedCityItems.contains(it)
    }

    return checkableCities?.filterIndexed { index, it ->
      (isElementLast(checkableCities.lastIndex, index) && it !is CountryItem) ||
      (!isElementLast(checkableCities.lastIndex, index) && !twoCountriesInARow(it, checkableCities[index+1]))
    }
  }

  private fun isElementLast(lastIndex: Int, elementIndex: Int): Boolean {
    return lastIndex == elementIndex
  }

  /**
   * Checks if there are two countries next to each other in a list
   */
  private fun twoCountriesInARow(e1: CountryCityItem, e2: CountryCityItem): Boolean{
    return e1 is CountryItem && e2 is CountryItem
  }
}