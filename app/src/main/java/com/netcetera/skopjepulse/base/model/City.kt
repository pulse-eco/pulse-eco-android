package com.netcetera.skopjepulse.base.model

import android.location.Location
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class City(
  val cityBorderPoints: List<Location>,
  @Json(name = "cityLocation")
  val location: Location,
  @Json(name = "cityName")
  val name: String,
  @Json(name = "siteName")
  val displayName: String,
  @Json(name = "intialZoomLevel")
  val initialWebZoomLevel: Int,
  val siteTitle: String,
  val siteUrl: String,
  val countryCode: String,
  val countryName: String
) : Parcelable {
  @Transient @IgnoredOnParcel val restUrl = "$siteUrl/rest/"
  @Transient @IgnoredOnParcel val intialZoomLevel = initialWebZoomLevel - 1

  @IgnoredOnParcel val cityBounds: LatLngBounds by lazy {
    cityBorderPoints.run {
      LatLngBounds(
        LatLng(minByOrNull { it.latitude }!!.latitude, minByOrNull { it.longitude }!!.longitude),
        LatLng(maxByOrNull { it.latitude }!!.latitude, maxByOrNull { it.longitude }!!.longitude)
      )
    }
  }

  companion object {
    fun macedoniaFirstComparator() = Comparator<City> { city1, city2 ->
      when {
        city1.countryCode == city2.countryCode -> compareValues(city1.name, city2.name)
        city1.countryCode == "MK" -> -1
        city2.countryCode == "MK" -> 1
        else -> compareValues(city1.countryName, city2.countryName)
      }
    }
  }
}