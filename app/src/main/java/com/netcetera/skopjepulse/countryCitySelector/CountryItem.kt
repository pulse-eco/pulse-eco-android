package com.netcetera.skopjepulse.countryCitySelector

/**
 *  [CountryItem]  model
 */
data class CountryItem (val countryName: String, val  countryCode: String, val listCityItem: MutableList<CityItem> = mutableListOf()) : CountryCityItem