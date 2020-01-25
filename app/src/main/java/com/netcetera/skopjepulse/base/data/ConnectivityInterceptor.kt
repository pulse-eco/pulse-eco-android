package com.netcetera.skopjepulse.base.data

import android.content.Context
import com.netcetera.skopjepulse.base.isOnline
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectivityInterceptor(private val mContext: Context) : Interceptor {

  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    if (!mContext.isOnline()) {
      throw NoConnectivityException()
    }

    return chain.proceed(chain.request())
  }
}

class NoConnectivityException : IOException() {
  override val message: String?
    get() = "No network connectivity exception"
}
