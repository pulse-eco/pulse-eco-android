package com.netcetera.skopjepulse.voronoi

import com.google.android.gms.maps.model.LatLng

typealias VoronoiMapVisualization = List<VoronoiMapRegion>

data class VoronoiMapRegion(val sites: List<LatLng>, val color: Int)