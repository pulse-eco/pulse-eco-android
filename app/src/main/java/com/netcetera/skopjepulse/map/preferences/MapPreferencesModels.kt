package com.netcetera.skopjepulse.map.preferences

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.netcetera.skopjepulse.map.preferences.DataVisualization.MARKERS
import com.netcetera.skopjepulse.map.preferences.DataVisualization.VORONOI
import com.netcetera.skopjepulse.map.preferences.MapType.DEFAULT

data class Preferences(
    val mapType: MapType,
    val dataVisualization: Set<DataVisualization>
) {
  companion object {
    internal fun default() = Preferences(
        DEFAULT, setOf(MARKERS, VORONOI))
  }
}

enum class MapType {
  DEFAULT, HIKEBIKEMAP, OPENTOPO
}

enum class DataVisualization {
  MARKERS, VORONOI
}

/**
 * Return the [onPassed] if the preference is satisfied or [onFailed] if it is not.
 */
fun <T> Preferences.filter(onPassed: T, onFailed: T, filterFn: (preferences: Preferences) -> Boolean) : T {
  return if(filterFn(this)) onPassed else onFailed
}

/**
 * [Preferences.filter], but with [LiveData]
 */
fun <T> LiveData<Preferences>.filter(onPassed: LiveData<T>, onFailed: LiveData<T>, filterFn: (preferences: Preferences) -> Boolean) : LiveData<T> {
  return Transformations.switchMap(this) {
    it.filter(onPassed, onFailed, filterFn)
  }
}