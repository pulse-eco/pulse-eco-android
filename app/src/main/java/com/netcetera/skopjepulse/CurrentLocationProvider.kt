package com.netcetera.skopjepulse

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.netcetera.skopjepulse.base.data.Resource
import java.util.concurrent.TimeUnit

class CurrentLocationProvider(context: Context, locationProvider: FusedLocationProviderClient) {

  private val _currentLocation = LocationLiveData(context, locationProvider)
  val currentLocation : LiveData<Resource<Location>>
    get() = _currentLocation

  fun requestLocation() {
    _currentLocation.tryRequestPermission()
  }

  private class LocationLiveData(private val context: Context, private val locationProvider: FusedLocationProviderClient) : LiveData<Resource<Location>>() {

    private val locationRequest : LocationRequest
    private val locationCallback : LocationCallback

    init {
      value = Resource.idle()
      locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        fastestInterval = TimeUnit.MINUTES.toMillis(1)
        interval = TimeUnit.MINUTES.toMillis(60)
      }

      locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
          if (result?.lastLocation != null) {
            value = Resource.success(result.lastLocation)
          } else {
            value = Resource.error(null, LocationServicesDisabled())
          }
        }

        override fun onLocationAvailability(availability: LocationAvailability?) {
          if (availability?.isLocationAvailable == false) {
            value = Resource.error(value?.data, LocationServicesDisabled())
          }
        }

      }
    }

    override fun onActive() {
      super.onActive()
      tryRequestPermission()
    }

    fun tryRequestPermission() {
      locationProvider.removeLocationUpdates(locationCallback)
      val hasPermission = ContextCompat.checkSelfPermission(context,
          Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
      if (hasPermission) {
        value = Resource.loading(value?.data)
        locationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
      } else {
        value = Resource.error(null, MissingLocationPermission())
      }
    }

    override fun onInactive() {
      locationProvider.removeLocationUpdates(locationCallback)
      super.onInactive()
    }
  }
}

class MissingLocationPermission : SecurityException()
class LocationServicesDisabled : RuntimeException()