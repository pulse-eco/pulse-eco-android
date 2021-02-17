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
import com.netcetera.skopjepulse.extensions.transformData
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * Implementation of [androidx.lifecycle.ViewModel] for the [CountryCitySelectorActivity] that is used for
 * displaying of data of a Country/Cities.
 */

class CountryCityViewModel(application: Application) : AndroidViewModel(application) {
  private val _countryCityList = MutableLiveData<ArrayList<Any>>()
  val countryCityList: LiveData<ArrayList<Any>>
    get() = _countryCityList

  var checkedCityItems : HashSet<CityItem>
  val sharedPref = application.getSharedPreferences(Constants.SELECTED_CITIES, Context.MODE_PRIVATE)

  init {
    val data = ArrayList<CountryItem>()
    data.add(CountryItem("Macedonia", "MK", mutableListOf(CityItem("Bitola"),CityItem("Kichevo"),CityItem("Kumanovo"),CityItem("Novoselo"),CityItem("Ohrid"),CityItem("Shtip"), CityItem("Skopje"),CityItem("Strumica"),CityItem("Tetovo"))))
    data.add(CountryItem("Bulgaria", "BG", mutableListOf(CityItem("Sofia"))))
    data.add(CountryItem("Greece", "GR", mutableListOf(CityItem("Thessaloniki"), CityItem("Syros"))))
    data.add(CountryItem("Ireland", "IR", mutableListOf(CityItem("Cork"))))
    data.add(CountryItem("Netherlands", "NE", mutableListOf(CityItem("Delft"))))
    data.add(CountryItem("Romania", "RO", mutableListOf(CityItem("Brasov"),CityItem("Bucharest"),CityItem("Cluj-Napoca"),CityItem("Codlea"),CityItem("Sacele"),CityItem("Targumures"))))
    data.add(CountryItem("Serbia", "SR", mutableListOf(CityItem("Nis"))))
    data.add(CountryItem("Switzerland", "SW", mutableListOf(CityItem("Zurich"))))
    data.add(CountryItem("USA", "USA", mutableListOf(CityItem("Portland"), CityItem("Grand-Rapids"))))

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