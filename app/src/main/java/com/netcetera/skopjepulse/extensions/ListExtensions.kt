package com.netcetera.skopjepulse.extensions

import com.netcetera.skopjepulse.countryCitySelector.CountryItem

/**
 * Transform from List of Countries to List of Countries and Cities for display
 */
fun List<Any>.transformData() : ArrayList<Any>{
  val dataTrans: List<Any>
  dataTrans = ArrayList()
  for (i in this){
    dataTrans.add(CountryItem((i as CountryItem).name, i.listCity))
    dataTrans.addAll((i as CountryItem).listCity)
  }
  return dataTrans
}