package com.netcetera.skopjepulse.map.mapvisualization

import com.netcetera.skopjepulse.base.model.Sensor
import com.netcetera.skopjepulse.map.model.MapMarkerModel

typealias OnSensorClicked = (Sensor) -> Unit

class MapMarkersController(private val mapView: org.osmdroid.views.MapView, private val onSensorClicked: OnSensorClicked) {
    private val markerProvider = MapMarkerProvider(mapView.context,   mapView)
    private val sensorMapMarkers: MutableSet<org.osmdroid.views.overlay.Marker> = HashSet()

    fun showMarkers(mapMarkerModels: List<MapMarkerModel>) {
      sensorMapMarkers.forEach{
        it.isEnabled = false
        it.closeInfoWindow()
      }
      sensorMapMarkers.clear()


      mapMarkerModels.forEach{
        sensorMapMarkers.add(markerProvider.generateMarker(it, onSensorClicked))
        }
      }

}
