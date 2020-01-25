package com.netcetera.skopjepulse.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netcetera.skopjepulse.base.data.Resource

fun <T> LiveData<T>.withEqualityCheck(): LiveData<T> {
  val sourceLiveData = this
  return MediatorLiveData<T>().apply {
    addSource(sourceLiveData) { newValue ->
      if (value != newValue) {
        value = newValue
      }
    }
  }
}

/**
 * Sets the value to the result of a function that is called when both `LiveData`s have data
 * or when they receive updates after that.
 */
fun <T, A, B> LiveData<A>.combine(other: LiveData<B>, onChange: (A, B) -> T): LiveData<T> {

  var source1emitted = false
  var source2emitted = false

  val result = MediatorLiveData<T>()

  val mergeF = {
    val source1Value = this.value
    val source2Value = other.value

    if (source1emitted && source2emitted) {
      result.value = onChange.invoke(source1Value!!, source2Value!!)
    }
  }

  result.addSource(this) { source1emitted = true; mergeF.invoke() }
  result.addSource(other) { source2emitted = true; mergeF.invoke() }

  return result
}

fun <T, A, B> LiveData<Resource<A>>.resourceCombine(other: LiveData<Resource<B>>,
    combineData: (A, B) -> T): LiveData<Resource<T>> {
  val result = MediatorLiveData<Resource<T>>()

  val mergeF = {
    val resource1 = this.value
    val resource2 = other.value

    // Combine data
    val resource1Data = resource1?.data
    val resource2Data = resource2?.data
    val combinedData = if (resource1Data != null && resource2Data != null) {
      combineData.invoke(resource1Data, resource2Data)
    } else null

    // Combine statuses
    val statuses = listOf(resource1?.status, resource2?.status)
    if (statuses.contains(Resource.Status.ERROR)) {
      result.value = Resource.error(combinedData, resource1?.throwable ?: resource2?.throwable)
    } else if (statuses.contains(Resource.Status.LOADING)) {
      result.value = Resource.loading(combinedData)
    } else if(statuses.all { it == Resource.Status.SUCCESS }) {
      result.value = Resource.success(combinedData!!)
    } else {
      result.value = Resource.idle(combinedData)
    }
  }

  result.addSource(this) { mergeF.invoke() }
  result.addSource(other) { mergeF.invoke() }

  return result
}