package com.netcetera.skopjepulse.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment

fun Context.toast(@StringRes resId : Int, duration : Int = Toast.LENGTH_SHORT) {
  Toast.makeText(this, resId, duration).show()
}

fun Fragment.toast(@StringRes resId : Int, duration : Int = Toast.LENGTH_SHORT) {
  requireContext().toast(resId, duration)
}

fun View.visible(shown : Boolean) {
  visibility = if (shown) View.VISIBLE else View.GONE
}

fun View.visible() = visible(true)

fun View.gone() = visible(false)

fun Context.openUrl(url: String) {
  val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
  startActivity(viewIntent)
}

const val DEFAULT_FADE_DURATION: Long = 100

fun View.fadeToVisible(duration: Long = DEFAULT_FADE_DURATION) {
  ViewCompat.animate(this)
    .setDuration(duration)
    .alpha(1f)
}

fun View.fadeToInvisible(duration: Long = DEFAULT_FADE_DURATION) {
  ViewCompat.animate(this)
    .setDuration(duration)
    .alpha(0f)
}