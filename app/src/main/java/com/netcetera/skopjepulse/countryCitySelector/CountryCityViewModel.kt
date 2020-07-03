package com.netcetera.skopjepulse.countryCitySelector

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.netcetera.skopjepulse.R
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

/**
 * Implementation of [androidx.lifecycle.ViewModel] for the [CountryCitySelectorActivity] that is used for
 * displaying of data of a Country/Cities.
 */

class CountryCityViewModel: ViewModel() {
  private lateinit var sb: StringBuilder
  private val _countryList = MutableLiveData<ArrayList<CountryItem>>()
  val countryList: LiveData<ArrayList<CountryItem>>
    get() = _countryList

  init {
    val data = ArrayList<CountryItem>()
    data.add(CountryItem("Macedonia", "MK", mutableListOf(City("Bitola"),City("Kichevo"),City("Kumanovo"),City("Novoselo"),City("Ohrid"),City("Shtip"), City("Skopje"),City("Strumica"),City("Tetovo"))))
    data.add(CountryItem("Serbia", "SR", mutableListOf(City("Beograd"), City("Nis"), City("Kraguevac"))))
    _countryList.value = data
  }


  fun onCityCheck(city: City, position: Int, isChecked : Boolean, context: Context){
    val sharedPref = context.getSharedPreferences(context.getString(R.string.selected_cities), Context.MODE_PRIVATE)
    var selected_cities : String?  = sharedPref.getString(context.getString(R.string.selected_cities), "")?.toLowerCase(Locale.ROOT)
    val selected_cities_list: ArrayList<String>? = selected_cities?.split(",")?.map { it.trim() } as ArrayList<String>?

    if (isChecked){
      selected_cities_list?.add(city.name.toLowerCase(Locale.ROOT))
    }

    else{
      selected_cities_list?.removeAt(selected_cities_list.indexOf(city.name.toLowerCase(Locale.ROOT)))
    }

    selected_cities = selected_cities_list?.joinToString(",")

    val editor: SharedPreferences.Editor = sharedPref.edit()
    editor.putString(context.getString(R.string.selected_cities), selected_cities);
    editor.commit()

  }

}