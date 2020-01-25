package com.netcetera.skopjepulse.base

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

class XAxisDateValueFormatter : ValueFormatter() {
  private val timeFormat: DateFormat
  private val dayFormat: DateFormat

  init {
    this.timeFormat = SimpleDateFormat("HH:mm", Locale.US)
    this.dayFormat = SimpleDateFormat("E dd", Locale.US)
  }

  override fun getAxisLabel(value: Float, axis: AxisBase): String {
    val calendar = Calendar.getInstance().apply {
      timeInMillis = value.roundToLong()
      val minuteGranularity = TimeUnit.MILLISECONDS.toMinutes(axis.granularity.roundToLong()).toInt()
      val minutes = get(Calendar.MINUTE)
      set(Calendar.MINUTE, minutes - (minutes % minuteGranularity))
    }

    if (calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.HOUR_OF_DAY) == 0) {
      return dayFormat.format(calendar.time)
    } else {
      return timeFormat.format(calendar.time)
    }
  }
}