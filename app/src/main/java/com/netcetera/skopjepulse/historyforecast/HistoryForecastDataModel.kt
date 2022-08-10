package com.netcetera.skopjepulse.historyforecast

import com.netcetera.skopjepulse.base.model.Band
import com.netcetera.skopjepulse.base.model.SensorReading

data class HistoryForecastDataModel(
  val averageWeeklyDataModel: SensorReading?,
  val sensorValueColor: Band?,
  val viewType: Int
)