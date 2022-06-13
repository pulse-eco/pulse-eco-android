package com.netcetera.skopjepulse.historyforecast.calendar

import com.netcetera.skopjepulse.base.model.Band
import com.netcetera.skopjepulse.base.model.SensorReading

data class CalendarItemDataModel(val averageWeeklyDataModel: SensorReading?, val sensorValueColor: Band?)