package com.netcetera.skopjepulse.base.model

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class CityOverall(
    val cityName: String,
    val values: Map<MeasurementType, String>
)