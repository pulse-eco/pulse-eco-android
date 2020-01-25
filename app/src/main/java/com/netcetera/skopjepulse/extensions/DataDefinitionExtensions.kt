package com.netcetera.skopjepulse.extensions

import androidx.annotation.ColorInt
import com.netcetera.skopjepulse.base.model.DataDefinition

@ColorInt
fun DataDefinition.interpolateColor(value: Int): Int {
  return findBandByValue(value).markerColor
//  if (value <= bands.first().from) {
//    return Color.parseColor(bands.first().legendColor)
//  }
//  if (value >= bands.last().from) {
//    return Color.parseColor(bands.last().legendColor)
//  }
//
//  val indexOfMatchingBand = bands.indexOfFirst { value.roundToInt() in it.from..it.to }
//  val indexOfNextBand = indexOfMatchingBand + 1
//
//  val partPerUnit = (bands[indexOfMatchingBand].from - bands[indexOfNextBand].from) / 100f
//  val partOfValue = (value - bands[indexOfMatchingBand].from) * partPerUnit
//
//  val colorFrom = Color.parseColor(bands[indexOfMatchingBand].legendColor)
//  val colorTo = Color.parseColor(bands[indexOfNextBand].legendColor)
//  return Interpolate.hsv(colorFrom, partOfValue.toFloat(), colorTo)
}