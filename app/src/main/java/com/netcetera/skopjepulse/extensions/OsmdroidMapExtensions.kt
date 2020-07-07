package com.netcetera.skopjepulse.extensions

import com.netcetera.skopjepulse.base.model.City
import org.osmdroid.api.IMapController
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView


fun MapView.updateForCity(city: City?) {
  if (city == null) {
    return
  }
  minZoomLevel = (city.intialZoomLevel - 1f).toDouble()
  maxZoomLevel = (city.intialZoomLevel + 3f).toDouble()

  val mapController: IMapController
  mapController = this.controller
  mapController.setZoom(city.intialZoomLevel.toDouble()+1.0)
  val startPoint = GeoPoint(city.location.latitude, city.location.longitude)
  mapController.setCenter(startPoint)

}
