package com.netcetera.skopjepulse.countryCitySelector

/**
 *  [City] and [Country] models
 */

data class City (val name: String)
data class Country (val name: String, val listCity: MutableList<City> = mutableListOf())