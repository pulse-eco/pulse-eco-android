package com.netcetera.skopjepulse.base.model

import android.location.Location
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.jsonAdapter.PanceLocation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
 data class Sensor(
  @Json(name = "sensorId")
  val id: String,
  @PanceLocation
  val position: Location?,
  @Json(name = "comments")
  val comments: String,
  @Json(name = "type")
  val type: Type,
  @Json(name = "description")
  val description: String,
  @Json(name = "status")
  val status: Status
) {
  // do not change, values from SkopjePulse RestApi
  @Keep
  enum class Status { ACTIVE, INACTIVE, NOT_CLAIMED }

  @Keep
  enum class Type(@DrawableRes val drawableRes: Int?) {
    MOEPP(R.drawable.ic_sensor_type_moepp),
    WIFI(R.drawable.ic_sensor_type_wifi),
    LORA(R.drawable.ic_sensor_type_lora),
    WIFI_V2(R.drawable.ic_sensor_type_wifi),
    UNKNOWN(null)
  }
}