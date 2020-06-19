package com.netcetera.skopjepulse.countryCitySelector

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Implementation of [androidx.lifecycle.ViewModel] for the [CountryCitySelectorActivity] that is used for
 * displaying of data of a Country/Cities.
 */

class CountryCityViewModel: ViewModel() {

  private val _countryList = MutableLiveData<ArrayList<Any>>()
  val countryList: LiveData<ArrayList<Any>>
    get() = _countryList

  init {
    val data = ArrayList<Any>()
    data.add(Country("Macedonia", mutableListOf(City("Stip"), City("Skopje"), City("Bitola"))))
    data.add(Country("Serbia", mutableListOf(City("Beograd"), City("Nish"), City("Kraguevac"))))
    _countryList.value = data
  }
}