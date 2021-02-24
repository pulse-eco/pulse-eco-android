package com.netcetera.skopjepulse.base.model

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class Country(
  val countryCode: String,
  val countryName: String,
  val cities: List<City>
)