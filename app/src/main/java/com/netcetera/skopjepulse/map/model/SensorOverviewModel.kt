package com.netcetera.skopjepulse.map.model

import com.netcetera.skopjepulse.base.model.Sensor
import java.util.Date

data class SensorOverviewModel(val sensor: Sensor, val measurement: Double, val measurementUnit: String,
                               val timestamp: Date, val favourite: Boolean)