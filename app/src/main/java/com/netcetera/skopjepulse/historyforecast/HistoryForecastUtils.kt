package com.netcetera.skopjepulse.historyforecast

import android.content.Context
import com.netcetera.skopjepulse.R

class HistoryForecastUtils {

  fun getMockColor(context: Context, pos: Int): Int {
    return when {
      pos % 3 == 0 -> {
        R.drawable.green_shape_with_radius
      }
      pos % 3 == 1 -> {
        R.drawable.orange_shape_with_radius
      }
      else -> {
        R.drawable.red_shape_radius
      }
    }
  }

  fun getMockPollutionAmount(context: Context, pos: Int): Int {
    return pos + 1 * 3
  }

  fun getMockDayName(context: Context, pos: Int): String {
    when {
      pos % 3 == 0 -> {
        return context.getString(R.string.monday_short)
      }
      pos % 3 == 1 -> {
        return context.getString(R.string.tuesday_short)
      }
      pos % 3 == 2 -> {
        return context.getString(R.string.wednesday_short)
      }
      pos % 3 == 3 -> {
        return context.getString(R.string.thursday_short)
      }
      pos % 3 == 4 -> {
        return context.getString(R.string.friday_short)
      }
      pos % 3 == 5 -> {
        return context.getString(R.string.saturday_short)
      }
      pos % 3 == 6 -> {
        return context.getString(R.string.sunday_short)
      }
      else -> {
        return context.getString(R.string.unknown)
      }
    }
  }
}