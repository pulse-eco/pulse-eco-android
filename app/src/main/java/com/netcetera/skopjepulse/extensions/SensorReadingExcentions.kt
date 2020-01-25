package com.netcetera.skopjepulse.extensions

import com.netcetera.skopjepulse.base.model.Sensor
import com.netcetera.skopjepulse.base.model.SensorReading
import java.util.Calendar

fun List<SensorReading>.avgOfLatest(maxStaleMinutes : Int = 60) : Double {
  val maxStaleTimestamp = Calendar.getInstance().apply {
    add(Calendar.MINUTE, -maxStaleMinutes)
  }.time
  return filter { it.stamp.after(maxStaleTimestamp) }.map { it.value }.average()
}

fun Map<Sensor, List<SensorReading>>.avgOfLatest(maxStaleMinutes : Int = 60) : Double {
  return map { (_, sensorReadings) -> sensorReadings.avgOfLatest(maxStaleMinutes) }
    .filter { !it.isNaN() }
    .average()
}