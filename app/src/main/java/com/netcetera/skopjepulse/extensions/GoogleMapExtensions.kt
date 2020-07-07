package com.netcetera.skopjepulse.extensions

import android.annotation.SuppressLint
import android.content.Context
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

import com.netcetera.skopjepulse.utils.PulseInfoWindowAdapter


fun GoogleMap.applyPulseStyling(context: Context) {
  applyStyling(context, MapStyleOptions.loadRawResourceStyle(context, R.raw.light_pulse_eco_maps_style))
}

fun GoogleMap.applyCitySelectPulseStyling(context: Context) {
  applyStyling(context, MapStyleOptions.loadRawResourceStyle(context, R.raw.light_pulse_eco_maps_style_city_select))
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