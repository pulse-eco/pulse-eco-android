package com.netcetera.skopjepulse.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.isLocationPermissionGranted
import com.netcetera.skopjepulse.base.model.City
import com.netcetera.skopjepulse.map.preferences.MapType
import com.netcetera.skopjepulse.map.preferences.MapType.DEFAULT
import com.netcetera.skopjepulse.map.preferences.MapType.SATELLITE
import com.netcetera.skopjepulse.map.preferences.MapType.TERRAIN
import com.netcetera.skopjepulse.utils.PulseInfoWindowAdapter

var GoogleMap.pulseMapType : MapType
  get() =
    when(mapType) {
      GoogleMap.MAP_TYPE_NORMAL -> DEFAULT
      GoogleMap.MAP_TYPE_SATELLITE -> SATELLITE
      GoogleMap.MAP_TYPE_TERRAIN -> TERRAIN
      else -> DEFAULT
    }
  set(value) {
    if (pulseMapType != value) {
      mapType = when(value) {
        DEFAULT -> GoogleMap.MAP_TYPE_NORMAL
        SATELLITE -> GoogleMap.MAP_TYPE_SATELLITE
        TERRAIN -> GoogleMap.MAP_TYPE_TERRAIN
      }
    }
  }

fun GoogleMap.applyPulseStyling(context: Context) {
  val currentNightMode = context.getResources().getConfiguration().uiMode and Configuration.UI_MODE_NIGHT_MASK
  when (currentNightMode) {
    Configuration.UI_MODE_NIGHT_NO -> {
      applyStyling(
        context,
        MapStyleOptions.loadRawResourceStyle(context, R.raw.light_pulse_eco_maps_style)
      )
    }
    Configuration.UI_MODE_NIGHT_YES -> {
      applyStyling(
        context,
        MapStyleOptions.loadRawResourceStyle(context, R.raw.dark_pulse_eco_maps_style)
      )
    }
  }

}

private fun GoogleMap.applyStyling(context: Context, style: MapStyleOptions) {
  setMapStyle(style)
  uiSettings.apply {
    isMapToolbarEnabled = false
    isRotateGesturesEnabled = false
    isTiltGesturesEnabled = false
    isMyLocationButtonEnabled = false
  }

  setInfoWindowAdapter(PulseInfoWindowAdapter(context))

  if (context.isLocationPermissionGranted()) {
    @SuppressLint("MissingPermission")
    isMyLocationEnabled = true
  }
}

fun GoogleMap.updateForCity(city: City?) {
  if (city == null) {
    return
  }
  setLatLngBoundsForCameraTarget(city.cityBounds)
  setMinZoomPreference(city.intialZoomLevel - 1f)
  setMaxZoomPreference(city.intialZoomLevel + 3f)

  val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
    LatLng(city.location.latitude, city.location.longitude),
    city.intialZoomLevel.toFloat())
  moveCamera(cameraUpdate)
}

fun GoogleMap.lifecycleAwareOnMapClickListener(lifecycleOwner: LifecycleOwner, onMapClickListener: GoogleMap.OnMapClickListener) {
    lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                ON_START -> setOnMapClickListener(onMapClickListener)
                ON_STOP -> setOnMapClickListener(null)
                ON_DESTROY -> lifecycleOwner.lifecycle.removeObserver(this)
            }
        }
    })
}