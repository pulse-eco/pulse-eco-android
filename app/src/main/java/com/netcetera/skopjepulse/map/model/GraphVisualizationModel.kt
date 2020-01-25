package com.netcetera.skopjepulse.map.model

import com.netcetera.skopjepulse.base.model.Sensor
import com.netcetera.skopjepulse.base.model.SensorReading

data class GraphVisualizationModel(val data: Map<Sensor, List<SensorReading>>) {
  companion object {
    val EMPTY = GraphVisualizationModel(emptyMap())
  }
}