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
  private val _countryList = MutableLiveData<ArrayList<Any>>()
  val countryList: LiveData<ArrayList<Any>>
    get() = _countryList

  var checkedCities : HashSet<City>
  val sharedPref = application.getSharedPreferences(Constants.SELECTED_CITIES, Context.MODE_PRIVATE)

  init {
    val data = ArrayList<CountryItem>()
    data.add(CountryItem("Macedonia", "MK", mutableListOf(City("Bitola"),City("Kichevo"),City("Kumanovo"),City("Novoselo"),City("Ohrid"),City("Shtip"), City("Skopje"),City("Strumica"),City("Tetovo"))))
    data.add(CountryItem("Serbia", "SR", mutableListOf(City("Beograd"), City("Nis"), City("Kraguevac"))))
    _countryList.value = data.transformData()

    checkedCities = HashSet()
    val gson = Gson()
    val selectedCities = sharedPref.getString(Constants.SELECTED_CITIES, "")
    val type = object: TypeToken<HashSet<City>>() {}.type
    checkedCities = gson.fromJson(selectedCities, type)
  }


  fun onCityCheck(city: City, isChecked : Boolean){
    if (isChecked){
      checkedCities.add(city)
    }
    else{
      checkedCities.remove(city)
    }

  }

  /**
   *  When the user clicks on the floating action button, checked cities add in sharedPreferences
   */
  fun saveCheckedCities(){
    val editor: SharedPreferences.Editor = sharedPref.edit()
    val gson = Gson()
    val jsonSelectedCities = gson.toJson(checkedCities)
    editor.putString(Constants.SELECTED_CITIES, jsonSelectedCities);
    editor.commit()
  }

}