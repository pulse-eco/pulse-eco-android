package com.netcetera.skopjepulse.map.model

data class GraphModel(val bands: List<GraphBand>, val data: List<GraphSeries>)

data class GraphBand(val from : Int, val to : Int, val color : Int)

data class GraphSeries(val measurements : List<Pair<Long, Double>>, val title : String, val color : Int)