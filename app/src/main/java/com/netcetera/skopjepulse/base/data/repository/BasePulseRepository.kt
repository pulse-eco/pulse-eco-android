package com.netcetera.skopjepulse.base.data.repository

import android.util.ArrayMap
import androidx.lifecycle.LiveData
import com.netcetera.skopjepulse.base.data.Resource
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Temporary solution for reusable of "smart" refreshing of resources in the repositories.
 */
abstract class BasePulseRepository {
  protected val refreshStamps: MutableMap<LiveData<out Resource<Any>>, Long> = ArrayMap()
  private val dataMaxStale = TimeUnit.MINUTES.toMillis(10)

  protected fun shouldRefresh(data: LiveData<out Resource<Any>>, forceRefresh: Boolean): Boolean {
    val stamp = refreshStamps[data]
    return when {
      data.value?.status == Resource.Status.LOADING -> false
      stamp != null && stamp > Date().time - dataMaxStale -> forceRefresh
      else -> true
    }
  }
}