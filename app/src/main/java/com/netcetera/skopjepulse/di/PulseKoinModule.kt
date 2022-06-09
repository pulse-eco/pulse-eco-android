package com.netcetera.skopjepulse.di

import com.google.android.gms.location.LocationServices
import com.netcetera.skopjepulse.CurrentLocationProvider
import com.netcetera.skopjepulse.base.PreferredCityStorage
import com.netcetera.skopjepulse.base.data.DataDefinitionProvider
import com.netcetera.skopjepulse.base.data.api.baseMoshiBuilder
import com.netcetera.skopjepulse.base.data.api.createCityPulseApiService
import com.netcetera.skopjepulse.base.data.api.createPulseApiService
import com.netcetera.skopjepulse.base.data.repository.CityPulseRepository
import com.netcetera.skopjepulse.base.data.repository.FavouriteSensorsStorageFactory
import com.netcetera.skopjepulse.base.data.repository.PulseRepository
import com.netcetera.skopjepulse.base.model.City
import com.netcetera.skopjepulse.cityselect.CitySelectViewModel
import com.netcetera.skopjepulse.countryCitySelector.CountryCityViewModel
import com.netcetera.skopjepulse.main.MainViewModel
import com.netcetera.skopjepulse.map.MapViewModel
import com.netcetera.skopjepulse.map.preferences.MapPreferencesStorage
import com.squareup.leakcanary.RefWatcher
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val appModule = module {
  single { RefWatcher.DISABLED }

  single { baseMoshiBuilder().build() }
  single { createPulseApiService(androidApplication(), get()) }
  factory { (city : City) -> createCityPulseApiService(androidApplication(), get(), city.restUrl) }

  single { LocationServices.getFusedLocationProviderClient(androidApplication()) }
  single { CurrentLocationProvider(androidApplication(), get()) }

  single { PulseRepository(get()) }
  factory { (city: City) -> CityPulseRepository(get(parameters = { parametersOf(city) })) }

  single { FavouriteSensorsStorageFactory(androidApplication()) }
  factory { (city : City) ->
    val factory : FavouriteSensorsStorageFactory = get()
    factory.storageForCity(city)
  }
  single { PreferredCityStorage(androidApplication()) }
  single { DataDefinitionProvider(androidApplication(), get(), get())}
  single {
    MapPreferencesStorage(
        androidApplication())
  }

  viewModel { (city : City) ->
    MapViewModel(
      city,
      get(),
      get { parametersOf(city) },
      get(),
      get { parametersOf(city) }
    )
  }
  viewModel { MainViewModel(get(), get(), get()) }

  viewModel {
    CitySelectViewModel(get(), get(), get())
  }

  viewModel {
    CountryCityViewModel(get(), get())
  }
}