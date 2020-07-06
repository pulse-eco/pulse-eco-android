package com.netcetera.skopjepulse.countryCitySelector

import java.util.*
import kotlin.collections.ArrayList

/**
 * Implementation of search filter in [CountryCitySelectorActivity]
 */

class CountryCityFilter (var constraint: CharSequence?, var data: List<Any>?) {
  fun filterCountryCity() : ArrayList<Any> {
    val charSearch = constraint.toString()
    var resultList = ArrayList<Any>()
    if (charSearch.isEmpty()) {
      resultList = data as ArrayList<Any>
    } else {

      for (country in data!!) {
        if (country is CountryItem) {
          if (country.countryName.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
            resultList.add(country)
            for (city in country.listCity) {
              resultList.add(city)
            }
          } else {
            var isContain = false
            resultList.add(country)
            for (city in country.listCity) {
              if (city.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                isContain = true
                resultList.add(city)
              }
            }
            if (isContain == false) {
              resultList.removeAt(resultList.size - 1)
            }
          }
        }
      }
    }
    return resultList
  }
}
