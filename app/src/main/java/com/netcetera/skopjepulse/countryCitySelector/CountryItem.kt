package com.netcetera.skopjepulse.countryCitySelector

/**
 *  [CountryItem]  model
 */
data class CountryItem (val name: String, val listCity: MutableList<City> = mutableListOf())