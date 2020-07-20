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

  private var checkedCityItems : HashSet<CityItem>
  private val sharedPref = application.getSharedPreferences(Constants.SELECTED_CITIES, Context.MODE_PRIVATE)

  init {
    val data = ArrayList<CountryItem>()
    data.add(CountryItem("Macedonia", "MK", mutableListOf(CityItem("Bitola","Macedonia"),CityItem("Kichevo","Macedonia"),CityItem("Kumanovo","Macedonia"),CityItem("Novoselo","Macedonia"),CityItem("Ohrid","Macedonia"),CityItem("Shtip","Macedonia"), CityItem("Skopje","Macedonia"),CityItem("Strumica","Macedonia"),CityItem("Tetovo","Macedonia"))))
    data.add(CountryItem("Bulgaria", "BG", mutableListOf(CityItem("Sofia","Bulgaria"))))
    data.add(CountryItem("Greece", "GR", mutableListOf(CityItem("Syros","Greece"))))
    data.add(CountryItem("Ireland", "IR", mutableListOf(CityItem("Cork","Ireland"))))
    data.add(CountryItem("Netherlands", "NE", mutableListOf(CityItem("Delft", "Netherlands"))))
    data.add(CountryItem("Romania", "RO", mutableListOf(CityItem("Brasov","Romania"),CityItem("Bucharest","Romania"),CityItem("Cluj-Napoca","Romania"),CityItem("Codlea","Romania"),CityItem("Sacele","Romania"),CityItem("Targumures","Romania"))))
    data.add(CountryItem("Serbia", "SR", mutableListOf(CityItem("Nis","Serbia"))))
    data.add(CountryItem("Switzerland", "SW", mutableListOf(CityItem("Zurich","Switzerland"))))
    data.add(CountryItem("USA", "USA", mutableListOf(CityItem("Grand-Rapids", "USA"))))

    checkedCityItems = HashSet()
    val gson = Gson()
    val selectedCities = sharedPref.getString(Constants.SELECTED_CITIES, "")
    val type = object: TypeToken<HashSet<CityItem>>() {}.type
    checkedCityItems = gson.fromJson(selectedCities, type)

    _countryCityList.value = data.transformData()
  }


  /**
   *  When the user clicks on the city, checked city add in sharedPreferences
   */
  fun saveCheckedCity(city: CityItem){
    checkedCityItems.add(city)
    val editor: SharedPreferences.Editor = sharedPref.edit()
    val gson = Gson()
    val jsonSelectedCities = gson.toJson(checkedCityItems)
    editor.putString(Constants.SELECTED_CITIES, jsonSelectedCities);
    editor.commit()
  }

}