package com.netcetera.skopjepulse.countryCitySelector

/**
 *  [CityItem]  model
 */

data class CityItem (val name: String, var country: String, var isChecked : Boolean = false) : CountryCityItem