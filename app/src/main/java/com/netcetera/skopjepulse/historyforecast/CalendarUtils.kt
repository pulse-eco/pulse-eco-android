package com.netcetera.skopjepulse.historyforecast

import android.content.Context
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.Constants.Companion.EEE
import com.netcetera.skopjepulse.Constants.Companion.d_MMM
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.historyforecast.calendar.CalendarAdapter
import com.netcetera.skopjepulse.map.MapFragment
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

object CalendarUtils {

  val SHORT_START = 0;
  val SHORT_END = 3;
  val EARLIEST_MEASUREMENT = "01/01/2017"

  fun getShortMonthNames(context: Context, ): Array<String> {
    return arrayOf(
      context.getString(R.string.january).substring(SHORT_START, SHORT_END),
      context.getString(R.string.february).substring(SHORT_START, SHORT_END),
      context.getString(R.string.march).substring(SHORT_START, SHORT_END),
      context.getString(R.string.april).substring(SHORT_START, SHORT_END),
      context.getString(R.string.may).substring(SHORT_START, SHORT_END),
      context.getString(R.string.june).substring(SHORT_START, SHORT_END),
      context.getString(R.string.july).substring(SHORT_START, SHORT_END),
      context.getString(R.string.august).substring(SHORT_START, SHORT_END),
      context.getString(R.string.september).substring(SHORT_START, SHORT_END),
      context.getString(R.string.october).substring(SHORT_START, SHORT_END),
      context.getString(R.string.november).substring(SHORT_START, SHORT_END),
      context.getString(R.string.december).substring(SHORT_START, SHORT_END)
    )
  }

  fun getFullMonthName(context: Context, short: String): String {
    when (short) {
      context.getString(R.string.january).substring(SHORT_START, SHORT_END) -> {
        return context.getString(R.string.january)
      }

      context.getString(R.string.february).substring(SHORT_START, SHORT_END) -> {
        return context.getString(R.string.february)
      }

      context.getString(R.string.march).substring(SHORT_START, SHORT_END) -> {
        return context.getString(R.string.march)
      }

      context.getString(R.string.april).substring(SHORT_START, SHORT_END) -> {
        return context.getString(R.string.april)
      }

      context.getString(R.string.may).substring(SHORT_START, SHORT_END) -> {
        return context.getString(R.string.may)
      }

      context.getString(R.string.june).substring(SHORT_START, SHORT_END) -> {
        return context.getString(R.string.june)
      }

      context.getString(R.string.july).substring(SHORT_START, SHORT_END) -> {
        return context.getString(R.string.july)
      }

      context.getString(R.string.august).substring(SHORT_START, SHORT_END) -> {
        return context.getString(R.string.august)
      }

      context.getString(R.string.september).substring(SHORT_START, SHORT_END) -> {
        return context.getString(R.string.september)
      }

      context.getString(R.string.october).substring(SHORT_START, SHORT_END) -> {
        return context.getString(R.string.october)
      }

      context.getString(R.string.november).substring(SHORT_START, SHORT_END) -> {
        return context.getString(R.string.november)
      }
      else -> {
        return context.getString(R.string.december)
      }

    }
  }

  fun getCalendarYears(): Array<String> {
    var startYear = getYear().year
    val endYear = (startYear).downTo(getCurrentDate().year).last

    val mutableListYears = mutableListOf<String>()
    while (startYear <= endYear) {
      mutableListYears.add(startYear.toString())
      startYear ++
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

  fun formatWeekday(context: Context?, date: Date?): String {
    return formatDate(date, EEE)
  }

  fun showDayAndMonth(context: Context?, date: Date?): String {
    return formatDate(date, d_MMM)
  }

  fun formatDate(date: Date?, format: String): String {
    val formatDate = SimpleDateFormat(format, Locale.getDefault())
    return formatDate.format(date!!)
  }

  fun createFormatter(format: String): SimpleDateFormat {
    return SimpleDateFormat(format, Locale.getDefault())
  }

  fun getDayOfWeek(month: LocalDate, locale: Locale): String {
    return month.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
  }

  fun capitalize(word: String?): String {
    return word?.substring(0, 1)?.toUpperCase(Locale.getDefault()) + word?.substring(1)
      ?.toLowerCase(Locale.getDefault())
  }

  fun concatenateMonthYear(month: String, year: Int): String {
    return "$month $year";
  }

  fun getCurrentDate(): LocalDate {
    return LocalDate.parse(
      "${LocalDate.now().dayOfMonth}/${LocalDate.now().monthValue}/${LocalDate.now().year}",
      DateTimeFormatter.ofPattern(Constants.LOCAL_DATE)
    )
  }

  fun getRangeOneWeek(): LocalDate {
    val date = LocalDate.parse(
      "${LocalDate.now().dayOfMonth}/${LocalDate.now().monthValue}/${LocalDate.now().year}",
      DateTimeFormatter.ofPattern(Constants.LOCAL_DATE)
    )
    return date.minusDays(8)
  }

  fun getYear(): LocalDate {
    return LocalDate.parse(EARLIEST_MEASUREMENT, DateTimeFormatter.ofPattern(Constants.LOCAL_DATE))
  }

  fun getWholeMonth(): LocalDate {
    return LocalDate.parse(
      "01/${LocalDate.now().monthValue}/${LocalDate.now().year}",
      DateTimeFormatter.ofPattern(Constants.LOCAL_DATE)
    )
  }


}