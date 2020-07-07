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

  var checkedCityItems : HashSet<CityItem>
  val sharedPref = application.getSharedPreferences(Constants.SELECTED_CITIES, Context.MODE_PRIVATE)

  init {
    val data = ArrayList<CountryItem>()
    data.add(CountryItem("Macedonia", "MK", mutableListOf(CityItem("Bitola"),CityItem("Kichevo"),CityItem("Kumanovo"),CityItem("Novoselo"),CityItem("Ohrid"),CityItem("Shtip"), CityItem("Skopje"),CityItem("Strumica"),CityItem("Tetovo"))))
    data.add(CountryItem("Serbia", "SR", mutableListOf(CityItem("Beograd"), CityItem("Nis"), CityItem("Kraguevac"))))
    _countryList.value = data.transformData()

    checkedCityItems = HashSet()
    val gson = Gson()
    val selectedCities = sharedPref.getString(Constants.SELECTED_CITIES, "")
    val type = object: TypeToken<HashSet<CityItem>>() {}.type
    checkedCityItems = gson.fromJson(selectedCities, type)
  }


  fun onCityCheck(cityItem: CityItem, isChecked : Boolean){
    cityItem.isChecked = isChecked
    if (isChecked){
      checkedCityItems.add(cityItem)
    }
    else{
      checkedCityItems.remove(cityItem)
    }

  }

  /**
   *  When the user clicks on the floating action button, checked cities add in sharedPreferences
   */
  fun saveCheckedCities(){
    val editor: SharedPreferences.Editor = sharedPref.edit()
    val gson = Gson()
    val jsonSelectedCities = gson.toJson(checkedCityItems)
    editor.putString(Constants.SELECTED_CITIES, jsonSelectedCities);
    editor.commit()
  }

}