package com.netcetera.skopjepulse.historyforecast.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.calendar_dialog.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


class CalendarDialog : DialogFragment() {

  companion object {
    var latestDateSelected: Long? = null
    var MONTH: Int? = 0
    var YEAR: Int? = 0
    var newMonth: String? = null
    var newYear: String? = null
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.calendar_dialog, container)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
    val date = LocalDate.parse("01/06/2022", formatter)
    val dow: DayOfWeek = date.dayOfWeek
    val lengthOfMonth = date.lengthOfMonth()
    val output: String = dow.getDisplayName(TextStyle.SHORT, Locale.US)
    val intValueForDow = intValueForDayOfWeek(output)

    val currentMonth = date.month.toString().toLowerCase(Locale.ROOT)
    val currentYear = date.year.toString()

    val currentMonthYear = "${newMonth ?: currentMonth} ${newYear ?: currentYear}"
    calendarMonthYearText.text = currentMonthYear

    val list = ArrayList<CalendarTry>()
    for (i in 0 until intValueForDow) {
      list.add(CalendarTry(0, 3))
    }
    for (j in 1..lengthOfMonth) {
      list.add(CalendarTry(j, 3))
    }


    val arrayOfMonths = arrayOf(
      requireContext().getString(R.string.january).substring(0, 3),
      requireContext().getString(R.string.february).substring(0, 3),
      requireContext().getString(R.string.march).substring(0, 3),
      requireContext().getString(R.string.april).substring(0, 3),
      requireContext().getString(R.string.may).substring(0, 3),
      requireContext().getString(R.string.june).substring(0, 3),
      requireContext().getString(R.string.july).substring(0, 3),
      requireContext().getString(R.string.august).substring(0, 3),
      requireContext().getString(R.string.september).substring(0, 3),
      requireContext().getString(R.string.october).substring(0, 3),
      requireContext().getString(R.string.november).substring(0, 3),
      requireContext().getString(R.string.december).substring(0, 3)
    )

    val arrayOfYear = arrayOf("2017", "2018", "2019", "2020", "2021", "2022")

    val layoutManager = object : GridLayoutManager(context, 7) {
      override fun supportsPredictiveItemAnimations(): Boolean {
        return false
      }
    }

    val recyclerView = calendarRecyclerView
    recyclerView.layoutManager = layoutManager
    recyclerView.adapter = CalendarAdapter(requireContext(), list, 3)
    recyclerView.suppressLayout(true)


    calendarMonthYearText.setOnClickListener {
      calendarDialogOkButton.visibility = View.GONE
      calendarLine.visibility = View.GONE
      calendarMonthYearText.visibility = View.GONE
      calendarYearPicker.visibility = View.VISIBLE
      calendarYearPicker.text = newYear ?: currentYear
      calendarPreviousArrow.visibility = View.GONE
      calendarNextArrow.visibility = View.GONE
      calendarHeader.visibility = View.GONE
      calendarRecyclerView.visibility = View.GONE
      monthYearPickerRecyclerView.visibility = View.VISIBLE
      val monthAdapter = CalendarMonthYearPickerAdapter(requireContext(), arrayOfMonths)
      monthYearPickerRecyclerView.layoutManager =
        StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
      monthYearPickerRecyclerView.adapter = monthAdapter
      monthAdapter.onItemClick = {
        newMonth = getFullMonthName(CalendarMonthYearPickerAdapter.MONTH_YEAR_VALUE)
        calendarLine.visibility = View.VISIBLE
        calendarYearPicker.visibility = View.GONE
        val newInputs = "${newMonth ?: currentMonth} ${newYear ?: currentYear}"
        calendarMonthYearText.visibility = View.VISIBLE
        calendarMonthYearText.text = newInputs
        calendarPreviousArrow.visibility = View.VISIBLE
        calendarNextArrow.visibility = View.VISIBLE
        calendarHeader.visibility = View.VISIBLE
        calendarRecyclerView.visibility = View.VISIBLE
        calendarDialogOkButton.visibility = View.VISIBLE
        monthYearPickerRecyclerView.visibility = View.GONE
      }

      //Year Picker
      calendarYearPicker.setOnClickListener {
        calendarYearPicker.visibility = View.GONE
        calendarMonthYearText.visibility = View.GONE
        calendarPreviousArrow.visibility = View.GONE
        calendarNextArrow.visibility = View.GONE
        calendarHeader.visibility = View.GONE
        calendarRecyclerView.visibility = View.GONE
        monthYearPickerRecyclerView.visibility = View.VISIBLE
        monthYearPickerRecyclerView.layoutManager =
          StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        val yearAdapter = CalendarMonthYearPickerAdapter(requireContext(), arrayOfYear)
        monthYearPickerRecyclerView.adapter = yearAdapter
        yearAdapter.onItemClick = {
          newYear = CalendarMonthYearPickerAdapter.MONTH_YEAR_VALUE
          calendarDialogOkButton.visibility = View.VISIBLE
          calendarLine.visibility = View.VISIBLE
          calendarYearPicker.visibility = View.GONE
          calendarMonthYearText.visibility = View.VISIBLE
          calendarMonthYearText.text = "${newMonth ?: currentMonth} ${newYear ?: currentYear}"
          calendarPreviousArrow.visibility = View.VISIBLE
          calendarNextArrow.visibility = View.VISIBLE
          calendarHeader.visibility = View.VISIBLE
          calendarRecyclerView.visibility = View.VISIBLE
          monthYearPickerRecyclerView.visibility = View.GONE
        }
      }
    }

    calendarDialogCancelButton.setOnClickListener {
      dismiss()
    }

  }

  private fun getFullMonthName(short: String): String {
    when (short) {
      "Jan" -> {
        return getString(R.string.january)
      }

      "Feb" -> {
        return getString(R.string.february)
      }

      "Mar" -> {
        return getString(R.string.march)
      }

      "Apr" -> {
        return getString(R.string.april)
      }

      "May" -> {
        return getString(R.string.may)
      }

      "Jun" -> {
        return getString(R.string.june)
      }

      "Jul" -> {
        return getString(R.string.july)
      }

      "Aug" -> {
        return getString(R.string.august)
      }

      "Sep" -> {
        return getString(R.string.september)
      }

      "Oct" -> {
        return getString(R.string.october)
      }

      "Nov" -> {
        return getString(R.string.november)
      }
      else -> {
        return getString(R.string.december)
      }


    }
  }

  private fun getMonthNumber(month: String): Int {
    when (month) {
      getString(R.string.january) -> {
        return 0
      }
      getString(R.string.february) -> {
        return 1
      }
      getString(R.string.march) -> {
        return 2
      }
      getString(R.string.april) -> {
        return 3
      }
      getString(R.string.may) -> {
        return 4
      }
      getString(R.string.june) -> {
        return 5
      }
      getString(R.string.july) -> {
        return 6
      }
      getString(R.string.august) -> {
        return 7
      }
      getString(R.string.september) -> {
        return 8
      }
      getString(R.string.october) -> {
        return 9
      }
      getString(R.string.november) -> {
        return 10
      }
      getString(R.string.december) -> {
        return 11
      }
      else -> {
        return Calendar.MONTH
      }
    }
  }

  private fun intValueForDayOfWeek(day: String): Int {
    when (day) {
      getString(R.string.monday_short) -> {
        return 0
      }
      getString(R.string.tuesday_short) -> {
        return 1
      }
      getString(R.string.wednesday_short) -> {
        return 2
      }
      getString(R.string.thursday_short) -> {
        return 3
      }
      getString(R.string.friday_short) -> {
        return 4
      }
      getString(R.string.saturday_short) -> {
        return 5
      }
      else -> {
        return 6
      }
    }
  }
}


