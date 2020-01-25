package com.netcetera.skopjepulse.map.overallbanner

import androidx.annotation.ColorInt

data class OverallBannerData(
    val title: String,
    val value: String,
    val valueUnit: String,
    val description: String,
    @ColorInt val backgroundColor: Int,
    val legend: Legend)

data class Legend(
    val value: Int,
    val bands: List<LegendBand>
)

data class LegendBand(val endAt : Int, @ColorInt val color: Int, val labelStart : String, val labelEnd : String)