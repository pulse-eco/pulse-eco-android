package com.netcetera.skopjepulse.base.utils

import com.netcetera.skopjepulse.base.model.SensorReading
import java.util.*

fun List<SensorReading>.lastReading(maxStaleMinutes : Int = 120) : SensorReading? {
  val maxStaleDate = Calendar.getInstance().apply { add(Calendar.MINUTE, -maxStaleMinutes) }.time
  return maxByOrNull { it.stamp }?.takeIf { it.stamp.after(maxStaleDate) }
}

fun SensorReading.getMonthAndDayFromStamp(): String {
  return stamp.toString().substring(4, 10)
}

fun SensorReading.getYearFromStamp(): String {
  return stamp.toString().substring(30, 34)
}