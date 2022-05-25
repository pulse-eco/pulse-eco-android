package com.netcetera.skopjepulse.historyAndForecast

import com.netcetera.skopjepulse.base.model.SensorReading

data class HistoryForecastDataModel(val averageWeeklyDataModel: SensorReading, val viewType: Int)