package com.netcetera.skopjepulse.base.data

class Resource<out T> {
  val status: Status
  val data: T?
  val throwable: Throwable?

  @Suppress("ConvertSecondaryConstructorToPrimary") // this should only be visible to the inner classes
  private constructor(status: Status, data: T?, throwable: Throwable?) {
    this.status = status
    this.data = data
    this.throwable = throwable
  }

  enum class Status { SUCCESS, ERROR, LOADING, IDLE }

  companion object {
    fun <T> success(data: T): Resource<T> = Resource(Status.SUCCESS, data, null)
    fun <T> error(data: T?, throwable: Throwable?): Resource<T> = Resource(Status.ERROR, data, throwable)
    fun <T> loading(data: T? = null): Resource<T> = Resource(Status.LOADING, data, null)
    fun <T> idle(data: T? = null): Resource<T> = Resource(Status.IDLE, data, null)
  }
}