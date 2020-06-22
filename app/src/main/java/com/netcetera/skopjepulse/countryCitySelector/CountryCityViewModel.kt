package com.netcetera.skopjepulse.countryCitySelector

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Implementation of [androidx.lifecycle.ViewModel] for the [CountryCitySelectorActivity] that is used for
 * displaying of data of a Country/Cities.
 */

class CountryCityViewModel: ViewModel() {

  private val _countryList = MutableLiveData<ArrayList<CountryItem>>()
  val countryList: LiveData<ArrayList<CountryItem>>
    get() = _countryList

  init {
    val data = ArrayList<CountryItem>()
    data.add(CountryItem("Macedonia", mutableListOf(City("Stip"), City("Skopje"), City("Bitola"))))
    data.add(CountryItem("Serbia", mutableListOf(City("Beograd"), City("Nish"), City("Kraguevac"))))
    _countryList.value = data
  }
}