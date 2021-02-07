package com.netcetera.skopjepulse.countryCitySelector

/**
 *  [CityItem]  model
 */

data class CityItem (val name: String, var isChecked : Boolean = false)

/**
 * Returns the country name for a city
 *
 * @param data [List<Any>]
 * @return [String]
 */
fun CityItem.getCountryName(data: List<Any>?): String{
  return data
    ?.filterIsInstance<CountryItem>()
    ?.first {it.listCityItem.contains(this) }
    ?.countryName ?: ""
}