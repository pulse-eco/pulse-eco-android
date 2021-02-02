package com.netcetera.skopjepulse.base.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.github.ajalt.timberkt.e
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.data.NoConnectivityException
import com.netcetera.skopjepulse.base.data.Resource
import com.netcetera.skopjepulse.base.data.Resource.Status.ERROR
import com.netcetera.skopjepulse.base.data.Resource.Status.IDLE
import com.netcetera.skopjepulse.base.data.Resource.Status.LOADING
import com.netcetera.skopjepulse.base.data.Resource.Status.SUCCESS
import com.squareup.leakcanary.RefWatcher
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.HashSet

/**
 * The pulse.eco base [ViewModel] that shall be used for the rest of the [ViewModel]s.
 */
@KoinApiExtension
abstract class BaseViewModel : ViewModel(), KoinComponent {
  private val refWatcher by inject<RefWatcher>()
  /**
   * Instance of Application [Context] that the usage of it shall be avoided.
   * Keeping it has no risk, since it is the lifecycle of the application itself.
   */
  protected val context by inject<Context>()

  /**
   * Provides information if this [ViewModel] is loading some resource.
   */
  val showLoading: LiveData<Boolean>
  /**
   * Generic error message that has no specific error handling.
   * Value of `null` means there is no error message to show.
   */
  val errorMessage: LiveData<String?>

  protected val errorResources: LiveResources = ErrorResources()
  protected val loadingResources: LiveResources = LoadingResources()

  init {
    showLoading = Transformations.distinctUntilChanged(
        Transformations.map(loadingResources) {
          it != null
        }
    )

    errorMessage = Transformations.distinctUntilChanged(
        Transformations.map(errorResources) {
          if (it != null) handleError(it) ?: context.getString(R.string.technical_error) else null
        }
    )
  }

  /**
   * Handler of [Resource] with [Resource.Status.ERROR] that are registered in [BaseViewModel.errorResources].
   * @return The text of the message that shall be shown. `null` if no error message is desired.
   */
  protected open fun handleError(resource: Resource<Any>): String? {
    return if (resource.throwable is NoConnectivityException) {
      context.getString(R.string.error_msg_no_internet_connection)
    } else {
      if (resource.throwable == null) {
        e { "Unhandled technical error." }
      } else {
        e(resource.throwable) { "Unhandled technical error." }
      }
      null
    }
  }

  /**
   * Request refresh of the data that this [ViewModel] is responsible for.
   * @param forceRefresh true if should ignore any caching or timestamping logic.
   */
  abstract fun refreshData(forceRefresh: Boolean = true)

  override fun onCleared() {
    super.onCleared()
    refWatcher.watch(this)
  }
}

/**
 * Utility aggregator of multiple [LiveData] with [Resource] data.
 */
abstract class LiveResources : LiveData<Resource<Unit>>() {
  private val mediatorLiveData = MediatorLiveData<Resource<Unit>>()
  protected val resources = HashSet<LiveData<Resource<Unit>>>()

  fun addResource(source: LiveData<Resource<Unit>>) {
    resources.add(source)
    mediatorLiveData.addSource(source, resourceObserver)
    onSourceChange()
  }

  fun removeResource(source: LiveData<Resource<Unit>>) {
    resources.remove(source)
    mediatorLiveData.removeSource(source)
    onSourceChange()
  }

  private val resourceObserver = Observer<Resource<Any>> {
    onSourceChange()
  }

  override fun onActive() {
    super.onActive()
    mediatorLiveData.observeForever(emptyObserver)
  }

  override fun onInactive() {
    mediatorLiveData.removeObserver(emptyObserver)
    super.onInactive()
  }

  private val emptyObserver = Observer<Resource<Any>> { }

  abstract fun onSourceChange()
}

/**
 * Implementation of [LiveResources] that changes it's value when there is change in
 * [Resource] with [Resource.Status.ERROR].
 */
class ErrorResources : LiveResources() {
  override fun onSourceChange() {
    value = resources.firstOrNull { it.value?.status == ERROR }?.value
  }
}

/**
 * Implementation of [LiveResources] that changes it's value when there is change in
 * [Resource] with [Resource.Status.LOADING].
 */
class LoadingResources : LiveResources() {
  override fun onSourceChange() {
    value = resources.firstOrNull { it.value?.status == LOADING }?.value
  }
}

private fun <T> LiveData<Resource<T>>.toLiveDataResource(
    customLoading: ((Resource<T>) -> Resource<Unit>?)? = null,
    customError: ((Resource<T>) -> Resource<Unit>?)? = null
): LiveData<Resource<Unit>> {
  return Transformations.switchMap(this) {
    MutableLiveData(when (it?.status) {
      SUCCESS -> Resource.success(Unit)
      ERROR -> customError?.invoke(it) ?: Resource.error(Unit, it.throwable)
      LOADING -> customLoading?.invoke(it) ?: Resource.loading()
      IDLE, null -> Resource.idle()
    })
  }
}

/**
 * Transform [LiveData] with [Resource] data into [LiveData] that can be passed into
 * [LiveResources.addResource] without cast warnings being shown.
 *
 * Subject to it being moved to the [LiveResources.addResource] and [LiveResources.removeResource]
 *
 * @param customLoading custom mapping logic that is applied when the resource is in [Resource.Status.LOADING] state.
 */
fun <T> LiveData<Resource<T>>.toLoadingLiveDataResource(
    customLoading: ((Resource<T>) -> Resource<Unit>?)? = null): LiveData<Resource<Unit>> {
  return toLiveDataResource(customLoading = customLoading)
}

/**
 * @see [toLoadingLiveDataResource]
 */
fun <T> LiveData<Resource<T>>.toErrorLiveDataResource(
    customError: ((Resource<T>) -> Resource<Unit>?)? = null): LiveData<Resource<Unit>> {
  return toLiveDataResource(customError = customError)
}