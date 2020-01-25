package com.netcetera.skopjepulse.cityselect

import androidx.annotation.ColorInt
import com.netcetera.skopjepulse.base.model.City

data class CitySelectItem(val city: City,
                          val measurementDescription: String,
                          val measurementValue: String,
                          val measurementUnit : String,
                          @ColorInt val color : Int)