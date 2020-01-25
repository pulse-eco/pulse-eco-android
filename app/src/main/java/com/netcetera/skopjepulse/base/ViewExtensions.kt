package com.netcetera.skopjepulse.base

import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned

val Int.dp: Int
  get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.dp2px: Float
  get() = (this * Resources.getSystem().displayMetrics.density)

val Int.sp: Float
 get() = (this / Resources.getSystem().displayMetrics.scaledDensity)

val Int.sp2px: Float
  get() = (this * Resources.getSystem().displayMetrics.scaledDensity)

@Suppress("DEPRECATION")
fun String.fromHtml() : Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
  Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
} else {
  Html.fromHtml(this)
}