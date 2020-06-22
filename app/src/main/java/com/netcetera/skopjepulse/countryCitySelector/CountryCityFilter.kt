package com.netcetera.skopjepulse.countryCitySelector

import com.netcetera.skopjepulse.extensions.transformData
import java.util.*
import kotlin.collections.ArrayList

class CountryCityFilter (var constraint: CharSequence?, var data: List<CountryItem>?) {
  fun filterCountryCity() : ArrayList<Any> {
    val charSearch = constraint.toString()
    var resultList = ArrayList<Any>()
    if (charSearch.isEmpty()) {
      resultList = data!!.transformData()
    } else {

      for (country in data!!) {
        if (country.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
          resultList.add(country)
          for (city in country.listCity) {
            resultList.add(city)
          }
        }
        else{
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
    return resultList
  }
}
