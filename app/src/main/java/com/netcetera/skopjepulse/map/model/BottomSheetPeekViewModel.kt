package com.netcetera.skopjepulse.map.model

data class BottomSheetPeekViewModel(val sensorOverviewModel: SensorOverviewModel?,
                                    val hasFavouriteSensors: Boolean,
                                    val canAddMoreFavouriteSensors: Boolean)