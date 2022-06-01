package com.netcetera.skopjepulse.historyforecast

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.calendar_dialog.*
import java.text.SimpleDateFormat
import java.util.*


class CalendarDialog : DialogFragment() {

  companion object {
    var latestDateSelected: Long? = null
    var MONTH: Int? = 0
    var YEAR: Int? = 0
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

    val arrayOfMonths = arrayOf(
      requireContext().getString(R.string.january),
      requireContext().getString(R.string.february),
      requireContext().getString(R.string.march),
      requireContext().getString(R.string.april),
      requireContext().getString(R.string.may),
      requireContext().getString(R.string.june),
      requireContext().getString(R.string.july),
      requireContext().getString(R.string.august),
      requireContext().getString(R.string.september),
      requireContext().getString(R.string.october),
      requireContext().getString(R.string.november),
      requireContext().getString(R.string.december)
    )

    val arrayOfYear = arrayOf("2017", "2018", "2019", "2020", "2021", "2022")

    val calendar = Calendar.getInstance()

    calendarViewPicker.setOnDateChangeListener { calView: CalendarView, year: Int, month: Int, dayOfMonth: Int ->
      val cal: Calendar = Calendar.getInstance()
      cal.set(year, month, dayOfMonth)
      calView.setDate(cal.timeInMillis, true, true)
    }

    if (latestDateSelected != null) {
      calendar.set(Calendar.YEAR, YEAR!!)
      calendar.set(Calendar.MONTH, MONTH!!)
      calendarViewPicker.setDate(latestDateSelected!!, false, false)
    }

    monthYearButtons.setOnClickListener {
      monthYearPickerRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
      val calendarAdapter = CalendarAdapter(requireContext(),arrayOfMonths)
      monthYearPickerRecyclerView.adapter = calendarAdapter
      val currentYear = calendar.get(Calendar.YEAR).toString()
      yearPicker.text = currentYear
      showRecyclerViewHideCalendar()
      yearPicker.isVisible = true

      calendarAdapter.onItemClick = {
        var newMonth = CalendarAdapter.MONTH_YEAR_VALUE
        MONTH = getMonthNumber(newMonth)
        calendar.set(Calendar.MONTH, getMonthNumber(newMonth))
        calendarViewPicker.setDate(calendar.timeInMillis, false, false)
        showCalendarHideRecyclerView()
        calendarCancelButton()
      }
      calendarDialogCancelButton.setOnClickListener {
        showCalendarHideRecyclerView()
        calendarCancelButton()
      }
      yearPicker.setOnClickListener {
        var newMonth = CalendarAdapter.MONTH_YEAR_VALUE
        monthYearPickerRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        val calendarAdapter = CalendarAdapter(requireContext(),arrayOfYear)
        monthYearPickerRecyclerView.adapter = calendarAdapter
        showRecyclerViewHideCalendar()
        yearPicker.isVisible = false
        calendarAdapter.onItemClick = {
          val newYear = CalendarAdapter.MONTH_YEAR_VALUE.toInt()
          MONTH = getMonthNumber(newMonth)
          YEAR = newYear
          if (newMonth == "") {
            monthYearPickerRecyclerView.layoutManager =
              StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            val monthAdapter = CalendarAdapter(requireContext(),arrayOfMonths)
            monthYearPickerRecyclerView.adapter = monthAdapter
            showRecyclerViewHideCalendar()
            monthAdapter.onItemClick = {
              newMonth = CalendarAdapter.MONTH_YEAR_VALUE
              MONTH = getMonthNumber(newMonth)
              calendar.set(Calendar.YEAR, newYear)
              calendar.set(Calendar.MONTH, getMonthNumber(newMonth))
              calendarViewPicker.setDate(calendar.timeInMillis, false, false)
              showCalendarHideRecyclerView()
              calendarCancelButton()
            }
            calendarCancelButtonFromMonthAndYearPicker()
          } else {
            calendar.set(Calendar.YEAR, newYear)
            calendar.set(Calendar.MONTH, getMonthNumber(newMonth))
            calendarViewPicker.setDate(calendar.timeInMillis, false, false)
            showCalendarHideRecyclerView()
            calendarCancelButton()
          }
        }
        calendarCancelButtonFromMonthAndYearPicker()
      }
    }
    calendarCancelButton()

    calendarDialogOkButton.setOnClickListener {

      val dateMillis: Long = calendarViewPicker.date

      val date = Date(dateMillis)
      val formatter = SimpleDateFormat(Constants.MONTH_DAY_YEAR_DATE_FORMAT)
      val datePicked = formatter.format(date.time)
      latestDateSelected = dateMillis
      Log.d("Date", datePicked)
      dismiss()
    }
  }

  private fun calendarCancelButtonFromMonthAndYearPicker() {
    calendarDialogCancelButton.setOnClickListener {
      showCalendarHideRecyclerView()
      calendarCancelButton()
    }
  }

  private fun calendarCancelButton() {
    calendarDialogCancelButton.setOnClickListener {
      dismiss()
    }
  }

  private fun showRecyclerViewHideCalendar() {
    calendarDialogOkButton.isVisible = false
    calendarViewPicker.isVisible = false
    monthYearPickerRecyclerView.isVisible = true
  }

  private fun showCalendarHideRecyclerView() {
    calendarDialogOkButton.isVisible = true
    calendarViewPicker.isVisible = true
    monthYearPickerRecyclerView.isVisible = false
    yearPicker.isVisible = false
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
      getString(R.string.april)-> {
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
}


