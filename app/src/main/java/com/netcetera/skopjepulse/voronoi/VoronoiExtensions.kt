package com.netcetera.skopjepulse.voronoi

import com.netcetera.skopjepulse.base.model.City
import org.kynosarges.tektosyne.geometry.RectD

val City.voronoiCityBounds: RectD
  get() {
    return RectD(
      cityBounds.southwest.latitude, cityBounds.southwest.longitude,
      cityBounds.northeast.latitude, cityBounds.northeast.longitude
    )
  }