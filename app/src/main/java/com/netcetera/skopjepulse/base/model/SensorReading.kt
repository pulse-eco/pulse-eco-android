package com.netcetera.skopjepulse.base.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@Keep
@JsonClass(generateAdapter = true)
data class SensorReading(
  @Json(name = "sensorId")
  val sensorId: String,
  @Json(name = "stamp")
  val stamp: Date,
  @Json(name = "type")
  val type: MeasurementType,
  @Json(name = "position")
  val position: String,
  @Json(name = "value")
  var value: Double
)