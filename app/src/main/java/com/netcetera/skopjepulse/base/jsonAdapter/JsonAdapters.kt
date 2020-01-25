package com.netcetera.skopjepulse.base.jsonAdapter

import android.annotation.SuppressLint
import android.location.Location
import com.netcetera.skopjepulse.base.model.Sensor
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import kotlin.annotation.AnnotationRetention.RUNTIME

@JsonClass(generateAdapter = true)
class LocationJson(val latitude: Double, val longitute: Double)
class LocationAdapter {
  @FromJson
  fun fromJson(locationJson : LocationJson): Location = Location("LocationJson").apply {
    latitude = locationJson.latitude
    longitude = locationJson.longitute
  }

  @ToJson
  fun toJson(latLng: Location?): LocationJson? = if (latLng == null) null else LocationJson(latLng.latitude, latLng.longitude)
}

@Retention(RUNTIME)
@JsonQualifier
annotation class PanceLocation

class PanceLocationAdapter {

  @FromJson
  @PanceLocation
  fun fromJson(latLngStr: String?): Location? = latLngStr?.split(",")?.let {
    Location("PanceLocationAdapter").apply {
      latitude = it[0].toDouble()
      longitude = it[1].toDouble()
    }
  }

  @ToJson
  fun toJson(@PanceLocation l: Location?): String? = if (l == null) null else "${l.latitude},${l.longitude}"
}

class SensorStatusAdapter {
  @SuppressLint("DefaultLocale")
  @FromJson
  fun fromJson(sensorActivityStr: String): Sensor.Status {
    return try {
      Sensor.Status.valueOf(sensorActivityStr.capitalize())
    } catch (e: IllegalArgumentException) {
      Sensor.Status.INACTIVE
    }
  }

  @ToJson
  fun toJson(sensorStatus: Sensor.Status): String = sensorStatus.toString()
}

class SensorTypeAdapter {
  @FromJson
  fun fromJson(sensorType: Int?): Sensor.Type = when (sensorType) {
    0 -> Sensor.Type.MOEPP
    1 -> Sensor.Type.LORA
    2 -> Sensor.Type.WIFI
    3 -> Sensor.Type.WIFI_V2
    else -> Sensor.Type.UNKNOWN
  }

  @ToJson
  fun toJson(sensorType: Sensor.Type): Int = when (sensorType) {
    Sensor.Type.MOEPP -> 0
    Sensor.Type.LORA -> 1
    Sensor.Type.WIFI -> 2
    Sensor.Type.WIFI_V2 -> 3
    Sensor.Type.UNKNOWN -> 99
  }

}