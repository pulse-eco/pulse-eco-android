package com.netcetera.skopjepulse.historyforecast

import android.content.Context
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.historyforecast.calendar.CalendarAdapter
import com.netcetera.skopjepulse.map.MapFragment
import java.time.LocalDate

object CalendarUtils {



  fun getCalendarMonthNames(context: Context): Array<String> {
    return arrayOf(
      context.getString(R.string.january).substring(0, 3),
      context.getString(R.string.february).substring(0, 3),
      context.getString(R.string.march).substring(0, 3),
      context.getString(R.string.april).substring(0, 3),
      context.getString(R.string.may).substring(0, 3),
      context.getString(R.string.june).substring(0, 3),
      context.getString(R.string.july).substring(0, 3),
      context.getString(R.string.august).substring(0, 3),
      context.getString(R.string.september).substring(0, 3),
      context.getString(R.string.october).substring(0, 3),
      context.getString(R.string.november).substring(0, 3),
      context.getString(R.string.december).substring(0, 3)
    )
  }

  //We have data from 2017:
  fun getCalendarYears(): Array<String> {
    val startDate = LocalDate.parse("01/01/2017", MapFragment.formatterLocalDate)
    val maxYear = (startDate.year).downTo(CalendarAdapter.DATE_INPUT_TODAY.year).last

    val mutableListYears = mutableListOf<String>()
    var year = startDate.year
    while (year <= maxYear) {
      mutableListYears.add(year.toString())
      year ++
    }
    return mutableListYears.toTypedArray()
  }

  fun getOrderFromWeekDay(day: String): Int {
    when (day) {
      "Mon" -> {
        return 0
      }
      "Tue" -> {
        return 1
      }
      "Wed" -> {
        return 2
      }
      "Thu" -> {
        return 3
      }
      "Fri" -> {
        return 4
      }
      "Sat" -> {
        return 5
      }
      else -> {
        return 6
      }
    }
  }

 fun getMonthInAppLanguage(context: Context, month: String): String {
    when (month) {
      "January" -> {
        return context.getString(R.string.january)
      }
      "February" -> {
        return  context.getString(R.string.february)
      }
      "March" -> {
        return  context.getString(R.string.march)
      }
      "April" -> {
        return  context.getString(R.string.april)
      }
      "May" -> {
        return  context.getString(R.string.may)
      }
      "June" -> {
        return  context.getString(R.string.june)
      }
      "July" -> {
        return  context.getString(R.string.july)
      }
      "August" -> {
        return  context.getString(R.string.august)
      }
      "September" -> {
        return  context.getString(R.string.september)
      }
      "October" -> {
        return  context.getString(R.string.october)
      }
      "November" -> {
        return  context.getString(R.string.november)
      }
      else -> {
        return  context.getString(R.string.december)
      }
    }
  }

  fun getOrderFromMonthName(context: Context, month: String?): Int? {
    when (month) {
      context.getString(R.string.january) -> {
        return 1
      }
      context.getString(R.string.february) -> {
        return 2
      }
      context.getString(R.string.march) -> {
        return 3
      }
      context.getString(R.string.april) -> {
        return 4
      }
      context.getString(R.string.may) -> {
        return 5
      }
      context.getString(R.string.june) -> {
        return 6
      }
      context.getString(R.string.july) -> {
        return 7
      }
      context.getString(R.string.august) -> {
        return 8
      }
      context.getString(R.string.september) -> {
        return 9
      }
      context.getString(R.string.october) -> {
        return 10
      }
      context.getString(R.string.november) -> {
        return 11
      }
      context.getString(R.string.december) -> {
        return 12
      }
      else -> {
        return null
      }
    }
  }

  fun getFullMonthName(context: Context, short: String): String {
    when (short) {
     context.getString(R.string.january).substring(0,3) -> {
        return context.getString(R.string.january)
      }

      context.getString(R.string.february).substring(0,3) -> {
        return context.getString(R.string.february)
      }

      context.getString(R.string.march).substring(0,3) -> {
        return context.getString(R.string.march)
      }

      context.getString(R.string.april).substring(0,3) -> {
        return context.getString(R.string.april)
      }

      context.getString(R.string.may).substring(0,3) -> {
        return context.getString(R.string.may)
      }

      context.getString(R.string.june).substring(0,3) -> {
        return context.getString(R.string.june)
      }

      context.getString(R.string.july).substring(0,3) -> {
        return context.getString(R.string.july)
      }

      context.getString(R.string.august).substring(0,3) -> {
        return context.getString(R.string.august)
      }

      context.getString(R.string.september).substring(0,3) -> {
        return context.getString(R.string.september)
      }

      context.getString(R.string.october).substring(0,3) -> {
        return context.getString(R.string.october)
      }

      context.getString(R.string.november).substring(0,3) -> {
        return context.getString(R.string.november)
      }
      else -> {
        return context.getString(R.string.december)
      }

    }
  }
}