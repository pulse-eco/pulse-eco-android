package com.netcetera.skopjepulse.base.data

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.base.data.api.PulseApiService
import com.netcetera.skopjepulse.base.model.DataDefinition
import com.netcetera.skopjepulse.base.model.MeasurementType
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
//import org.jetbrains.anko.defaultSharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Date

class DataDefinitionProvider(val context: Context, val moshi: Moshi, private val pulseApiService: PulseApiService) {

  private val _definitionsMap: MutableMap<MeasurementType, MutableLiveData<DataDefinition>> = HashMap()
  var definitions: LiveData<List<DataDefinition>> = MutableLiveData()

  init {
    loadData()
  }

  fun loadData() {
    val persistedMeasuresProvider = PersistedMeasuresProvider(context, moshi)
    val assetsMeasuresProvider = AssetsMeasuresProvider(context, moshi)

    val offlineDefinitions =
      persistedMeasuresProvider.persistedDefinitions?.second ?: assetsMeasuresProvider.definitions
      ?: emptyList()
    val mutableDefinitions = MutableLiveData(offlineDefinitions)
    definitions = mutableDefinitions

    offlineDefinitions.forEach {
      _definitionsMap.getOrPut(it.id) { MutableLiveData() }.value = it
    }

    val lang = context.getSharedPreferences(Constants.LANGUAGE_CODE, Context.MODE_PRIVATE)
      .getString(Constants.LANGUAGE_CODE, null)
    pulseApiService.measures((lang)).enqueue(object : Callback<List<DataDefinition>> {
      override fun onResponse(
        call: Call<List<DataDefinition>>,
        response: Response<List<DataDefinition>>
      ) {
        response.body()?.let { dataDefinitions ->
          mutableDefinitions.value = dataDefinitions
          dataDefinitions.forEach {
            _definitionsMap.getOrPut(it.id) { MutableLiveData() }.value = it
          }
          persistedMeasuresProvider.persistDefinitions(dataDefinitions)
        }
      }

      override fun onFailure(call: Call<List<DataDefinition>>, t: Throwable) {
        // maybe retry?
      }
    })
  }

  operator fun get(type: MeasurementType): LiveData<DataDefinition> {
    return _definitionsMap.getOrPut(type) { MutableLiveData() }
  }
}

private const val PERSISTED_TIMESTAMP = "persisted-data-definition-timestamp"
private const val PERSISTED_DEFINITIONS = "persisted-data-definitions"

class PersistedMeasuresProvider(context: Context, moshi: Moshi) {
//  private val sharedPreferences = context.defaultSharedPreferences
  val jsonAdapter: JsonAdapter<List<DataDefinition>> = moshi.adapter<List<DataDefinition>>(
      Types.newParameterizedType(List::class.java, DataDefinition::class.java))

  val persistedDefinitions: Pair<Date, List<DataDefinition>>?
    get() {
      val timestamp = Date(sharedPreferences.getLong(PERSISTED_TIMESTAMP, Long.MIN_VALUE))

      return sharedPreferences.getString(PERSISTED_DEFINITIONS, null)?.let { json ->
        try {
          jsonAdapter.fromJson(json)
        } catch (e: Exception) {
          Timber.w(e) { "Failed parsing persisted measures" }
          null
        }
      }?.let {
        Pair(timestamp, it)
      }
    }

  fun persistDefinitions(definitions: List<DataDefinition>) {
    val timestamp = Date().time
    val json = jsonAdapter.toJson(definitions)
    sharedPreferences.edit {
      putLong(PERSISTED_TIMESTAMP, timestamp)
      putString(PERSISTED_DEFINITIONS, json)
    }
  }
}

private class AssetsMeasuresProvider(context: Context, moshi: Moshi) {
  val definitions: List<DataDefinition>? by lazy {
    val jsonAdapter = moshi.adapter<List<DataDefinition>>(
        Types.newParameterizedType(List::class.java, DataDefinition::class.java))
    BufferedReader(InputStreamReader(context.assets.open("measures.json"))).use {
      it.readText()
    }.let { json ->
      try {
        jsonAdapter.fromJson(json)
      } catch (e: Exception) {
        Timber.w(e) { "Failed parsing measures.json from Assets" }
        null
      }
    }
  }
}