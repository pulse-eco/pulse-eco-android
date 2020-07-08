package com.netcetera.skopjepulse.extensions

import com.netcetera.skopjepulse.countryCitySelector.CityItem
import com.netcetera.skopjepulse.countryCitySelector.CountryItem

/**
 * Transform from List of Countries to List of Countries and Cities for display
 */
fun List<CountryItem>.transformData(checkedCityItems:HashSet<CityItem>) : ArrayList<Any>{
  val dataTrans: List<Any>
  dataTrans = ArrayList()
  for (country in this){
    dataTrans.add(CountryItem(country.countryName, country.countryCode, country.listCityItem))
    for (city in country.listCityItem){
      if (checkedCityItems.map { e -> e.name}.contains(city.name)){
        city.isChecked = true
      }
      dataTrans.add(city)
    }
  }
  return dataTrans
}