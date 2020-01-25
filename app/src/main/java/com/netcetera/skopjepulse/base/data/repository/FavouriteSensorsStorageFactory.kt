package com.netcetera.skopjepulse.base.data.repository

import android.content.Context
import com.netcetera.skopjepulse.base.model.City

class FavouriteSensorsStorageFactory(private val context: Context) {

  fun storageForCity(city: City) = FavouriteSensorsStorage(context, city)

}