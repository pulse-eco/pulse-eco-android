package com.netcetera.skopjepulse.base.model

import androidx.annotation.ColorInt
import androidx.annotation.Keep
import com.netcetera.skopjepulse.base.jsonAdapter.PulseEcoColor
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlin.math.roundToInt

typealias MeasurementType = String

@Keep
@JsonClass(generateAdapter = true)
data class DataDefinition(
    val id: MeasurementType, //noiseDataDefinition
    @Json(name = "buttonTitle") val shortTitle: String, //Noise
    @Json(name = "title") val longTitle: String, //Noise
    val description: String, //Noise measured
    val legendMin: Int, //0
    val legendMax: Int, //160
    val unit: String, //units
    val bands: List<Band>
) {
  @Transient
  private val minBand: Band = bands.minBy { it.from }!!

  fun findBandByValue(value: Double) : Band = findBandByValue(value.roundToInt())

  fun findBandByValue(value: Int): Band {
    return if (minBand.from > value) {
      minBand
    } else {
      bands.firstOrNull { value in it.from..it.to } ?: bands.last()
    }
  }
}

@Keep
@JsonClass(generateAdapter = true)
data class Band(
    val from: Int, //0
    val to: Int, //20
    @PulseEcoColor @ColorInt val legendColor: Int, //#00ab02
    @PulseEcoColor @ColorInt val markerColor: Int, //#00ab02
    val shortGrade: String, //Overall silence. Human hearing can barely register noticeable noise.
    val grade: String, //Overall silence. Human hearing can barely register noticeable noise.
    val suggestion: String //Enjoy the peace and quiet.
)