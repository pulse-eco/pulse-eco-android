package com.netcetera.skopjepulse.map.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.jetbrains.anko.defaultSharedPreferences

private const val PREFERENCES_KEY = "map-layer-picker-preferences"
private const val PREFERENCES_KEY_MAP_TYPE = "$PREFERENCES_KEY-map-type"
private const val PREFERENCES_KEY_VISUALIZATION = "$PREFERENCES_KEY-data-visualization"

class MapPreferencesStorage(context: Context) {
  private val storage: SharedPreferences = context.defaultSharedPreferences

  private val _preferences: MutableLiveData<Preferences>
  val preferences: LiveData<Preferences>
    get() = _preferences

  init {
    val default = Preferences.default()

    val mapType = storage.readPersistedMapType() ?: default.mapType
    val dataVisualization = storage.readDataVisualization()
        ?: default.dataVisualization

    _preferences = MutableLiveData(
        Preferences(mapType,
            dataVisualization))
  }

  fun updatePreferences(newPreferences: Preferences) {
    storage.edit {
      persistMapType(newPreferences.mapType)
      persistDataVisualization(newPreferences.dataVisualization)
    }
    _preferences.value = newPreferences
  }

  private fun SharedPreferences.readPersistedMapType(): MapType? {
    return getString(
        PREFERENCES_KEY_MAP_TYPE, null)?.let {
      MapType.valueOf(it)
    }
  }

  private fun SharedPreferences.Editor.persistMapType(mapType: MapType) {
    putString(PREFERENCES_KEY_MAP_TYPE, mapType.name)
  }

  private fun SharedPreferences.readDataVisualization(): Set<DataVisualization>? {
    return getStringSet(
        PREFERENCES_KEY_VISUALIZATION, null)?.map {
      DataVisualization.valueOf(it)
    }?.toSet()
  }

  private fun SharedPreferences.Editor.persistDataVisualization(
      dataVisualization: Set<DataVisualization>) {
    putStringSet(
        PREFERENCES_KEY_VISUALIZATION, dataVisualization.map { it.name }.toSet())
  }
}