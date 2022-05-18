package com.netcetera.skopjepulse.historyAndForecast

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

  companion object{
    var latestDateSelected : Long ? = null
    var MONTH : Int? = 0
    var YEAR: Int?=0
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
    val arrayOfMonths = arrayOf("January", "February", "March", "April", "May", "June", "July", "Avgust", "September", "October", "November", "December")
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
      val calendarAdapter = CalendarAdapter(arrayOfMonths)
      monthYearPickerRecyclerView.adapter = calendarAdapter

      val currentYear = calendar.get(Calendar.YEAR).toString()
      yearPicker.text = currentYear
      showRecyclerViewHideCalendar()

      calendarDialogOkButton.setOnClickListener {
        val newMonth = CalendarAdapter.MONTH_YEAR_VALUE
        MONTH = getMonthName(newMonth)
        calendar.set(Calendar.MONTH, getMonthName(newMonth))
        calendarViewPicker.setDate(calendar.timeInMillis, false, false)

        showCalendarHideRecyclerView()
        calendarCancelAndOkButtons()
      }

      calendarDialogCancelButton.setOnClickListener {
        showCalendarHideRecyclerView()
        calendarCancelAndOkButtons()
      }

      yearPicker.setOnClickListener {

        val newMonth = CalendarAdapter.MONTH_YEAR_VALUE
        monthYearPickerRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)

        val calendarAdapter = CalendarAdapter(arrayOfYear)
        monthYearPickerRecyclerView.adapter = calendarAdapter

        showRecyclerViewHideCalendar()

        calendarAdapter.onItemClick = {
          val newYear = CalendarAdapter.MONTH_YEAR_VALUE.toInt()
          MONTH = getMonthName(newMonth)
          YEAR = newYear
          calendar.set(Calendar.YEAR, newYear)
          calendar.set(Calendar.MONTH, getMonthName(newMonth))
          calendarViewPicker.setDate(calendar.timeInMillis, false, false)
        }

        calendarDialogCancelButton.setOnClickListener {
          showCalendarHideRecyclerView()
          calendarCancelAndOkButtons()
        }
        calendarDialogOkButton.setOnClickListener {
          showCalendarHideRecyclerView()
          calendarCancelAndOkButtons()
        }
      }
    }

    calendarCancelAndOkButtons()
  }

  private fun calendarCancelAndOkButtons()
  {

    calendarDialogOkButton.setOnClickListener {

      val dateMillis: Long = calendarViewPicker.date

      val date: Date = Date(dateMillis)
      val formatter = SimpleDateFormat(Constants.MONTH_DAY_YEAR_DATE_FORMAT)
      val datePicked = formatter.format(date.time)
      latestDateSelected = dateMillis
      Log.d("Date", datePicked)
      dismiss()
    }

    calendarDialogCancelButton.setOnClickListener {
      dismiss()
    }

  }
  private fun showRecyclerViewHideCalendar()
  {
    monthYearButtons.isVisible = false
    calendarViewPicker.isVisible = false
    monthYearPickerRecyclerView.isVisible = true
    yearPicker.isVisible = true
  }

  private fun showCalendarHideRecyclerView(){
    monthYearButtons.isVisible = true
    calendarViewPicker.isVisible = true
    monthYearPickerRecyclerView.isVisible = false
    yearPicker.isVisible = false
  }

  private fun getMonthName(month: String): Int {
    when (month) {
      "January" -> {
        return 0
      }
      "February" -> {
        return 1
      }
      "March" -> {
        return 2
      }
      "April" -> {
        return 3
      }
      "May" -> {
        return 4
      }
      "June" -> {
        return 5
      }
      "July" -> {
        return 6
      }
      "Avgust" -> {
        return 7
      }
      "September" -> {
        return 8
      }
      "October" -> {
        return 9
      }
      "November" -> {
        return 10
      }
      "December" -> {
        return 11
      }
      else -> {
        return 0
      }
    }
  }


}


