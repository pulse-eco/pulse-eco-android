package com.netcetera.skopjepulse.map

import android.graphics.Color

object ColorGenerator {
  fun generateNDistinctColors(n: Int): List<Int> {
    if (n > 0) {
      return (0..360 step 360 / n).map { Color.HSVToColor(floatArrayOf(it.toFloat(), 0.65f, 1f)) }
    } else {
      return emptyList()
    }
  }
}