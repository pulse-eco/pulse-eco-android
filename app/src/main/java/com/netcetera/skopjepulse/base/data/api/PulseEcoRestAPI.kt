package com.netcetera.skopjepulse.base.data.api

import android.content.Context
import androidx.annotation.Keep
import com.github.ajalt.timberkt.i
import com.netcetera.skopjepulse.base.data.ConnectivityInterceptor
import com.netcetera.skopjepulse.base.jsonAdapter.LocationAdapter
import com.netcetera.skopjepulse.base.jsonAdapter.PanceLocationAdapter
import com.netcetera.skopjepulse.base.jsonAdapter.PulseEcoColorAdapter
import com.netcetera.skopjepulse.base.jsonAdapter.SensorStatusAdapter
import com.netcetera.skopjepulse.base.jsonAdapter.SensorTypeAdapter
import com.netcetera.skopjepulse.base.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date

/**
 * Interface for the pulse.eco REST API.
 */
@Keep
interface PulseApiService {

  @GET("city")
  fun cities(): Call<List<City>>

  @GET("country")
  fun countries(): Call<List<Country>>

  @GET("overall")
  fun citiesOverall(@Query("cityNames") cityNames: List<String>) : Call<List<CityOverall>>

  @GET("measures")
  fun measures(@Query("lang") lang: String?): Call<List<DataDefinition>>
}

/**
 * Interface for the cities of pulse.eco REST APIs.
 */
@Keep
interface CityPulseApiService {
  @GET("data24h")
  fun getData24h(): Call<List<SensorReading>>

  @GET("sensor")
  fun getSensors(): Call<List<Sensor>>

  @GET("current")
  fun getCurrent() : Call<List<SensorReading>>

  @GET("avgData/day")
  fun getAvgDailyData(
    @Query("sensorId")sensorId :String,
    @Query("type")type:String,
    @Query("from")from:String,
    @Query("to")to:String
  ) : Call<List<SensorReading>>

  @GET("dataRaw")
  fun getSensorValuesForValueType(
    @Query("type")type:String,
    @Query("from")from:String,
    @Query("to")to:String
  ) : Call<List<SensorReading>>

  @GET("avgData/month")
  fun getAvgMonthData(
    @Query("sensorId")sensorId :String,
    @Query("type")type:String,
    @Query("from")from:String,
    @Query("to")to:String
  ) : Call<List<SensorReading>>
}

private const val PULSE_BASE_URL: String = "https://pulse.eco/rest/"

/**
 * Create instance of [PulseApiService].
 */
fun createPulseApiService(context: Context, moshi: Moshi): PulseApiService =
  baseRetrofitBuilder(moshi, PULSE_BASE_URL)
    .client(baseHttpClient(context).build())
    .build().create(PulseApiService::class.java)

/**
 * Create instance of [CityPulseApiService].
 */
fun createCityPulseApiService(context: Context, moshi: Moshi, cityUrl : String): CityPulseApiService =
  baseRetrofitBuilder(moshi, cityUrl)
    .client(baseHttpClient(context).build())
    .build().create(CityPulseApiService::class.java)

/**
 * [Moshi] JSON parser with additional JSON adapters for parsing data from the APIs.
 */
fun baseMoshiBuilder(): Moshi.Builder =
  Moshi.Builder()
    .add(LocationAdapter())
    .add(PanceLocationAdapter())
    .add(PulseEcoColorAdapter())
    .add(Date::class.java, Rfc3339DateJsonAdapter())
    .add(SensorStatusAdapter())
    .add(SensorTypeAdapter())

private fun baseRetrofitBuilder(moshi: Moshi, apiUrl : String) = Retrofit.Builder()
  .baseUrl(apiUrl)
  .addConverterFactory(MoshiConverterFactory.create(
    moshi
  ))

private fun baseHttpClient(context: Context) = OkHttpClient.Builder()
  .addInterceptor(HttpLoggingInterceptor(TimberHttpLogger).apply {
    level = HttpLoggingInterceptor.Level.BASIC
  })
  .addInterceptor(ConnectivityInterceptor(context))
  .retryOnConnectionFailure(true)

object TimberHttpLogger : HttpLoggingInterceptor.Logger {
  override fun log(message: String) {
    i { message }
  }
}