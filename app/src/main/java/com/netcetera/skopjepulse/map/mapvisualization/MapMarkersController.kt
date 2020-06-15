package com.netcetera.skopjepulse.map.mapvisualization

import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.Marker
import com.netcetera.skopjepulse.base.model.Sensor
import com.netcetera.skopjepulse.map.model.MapMarkerModel

typealias OnSensorClicked = (Sensor) -> Unit
//class MapMarkersController(private val mapView: MapView, private val onSensorClicked: OnSensorClicked) {
//  private val markerProvider = MapMarkerProvider(mapView.context)
//  private val sensorMapMarkers: MutableSet<Marker> = HashSet()
//
//  fun showMarkers(mapMarkerModels: List<MapMarkerModel>) {
//    mapView.getMapAsync { googleMap ->
//      sensorMapMarkers.forEach { it.remove() }
//      sensorMapMarkers.clear()
//      sensorMapMarkers.addAll(
//        mapMarkerModels.map { mapMarkerModel ->
//          googleMap.addMarker(markerProvider.generateMarker(mapMarkerModel)).apply {
//            tag = mapMarkerModel.sensor
//          }
//        })
//
//      googleMap.setOnMarkerClickListener { selectedMarker ->
//        val selectedSensor = selectedMarker.tag as Sensor
//        onSensorClicked.invoke(selectedSensor)
//        return@setOnMarkerClickListener false
//      }
//    }
//  }
//
//}

class MapMarkersController(private val mapView: org.osmdroid.views.MapView, private val onSensorClicked: OnSensorClicked) {
    private val markerProvider = MapMarkerProvider(mapView.context,   mapView)
    //private val sensorMapMarkers: MutableSet<Marker> = HashSet()
    private val sensorMapMarkers: MutableSet<org.osmdroid.views.overlay.Marker> = HashSet()

    fun showMarkers(mapMarkerModels: List<MapMarkerModel>) {
//      mapView.getMapAsync { googleMap ->
//        sensorMapMarkers.forEach { it.remove() }
//        sensorMapMarkers.clear()
//        sensorMapMarkers.addAll(
//          mapMarkerModels.map { mapMarkerModel ->
//            googleMap.addMarker(markerProvider.generateMarker(mapMarkerModel)).apply {
//              tag = mapMarkerModel.sensor
//            }
//          })

      sensorMapMarkers.forEach{
        it.isEnabled = false
        it.closeInfoWindow()
      }
      sensorMapMarkers.clear()


      mapMarkerModels.forEach{
        sensorMapMarkers.add(markerProvider.generateMarker(it, onSensorClicked))


//      mapMarkerModels.map { mapMarkerModel ->
//        markerProvider.generateMarker(mapMarkerModel, onSensorClicked)
//        sensorMapMarkers.add(...)


//        googleMap.setOnMarkerClickListener { selectedMarker ->
//          val selectedSensor = selectedMarker.tag as Sensor
//          onSensorClicked.invoke(selectedSensor)
//          return@setOnMarkerClickListener false
        }
      }
  //  }
}
