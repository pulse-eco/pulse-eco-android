package com.netcetera.skopjepulse.map.model

import com.netcetera.skopjepulse.base.model.Sensor

data class MapMarkerModel(val sensor: Sensor, val measuredValue: Int, val markerColor: Int)