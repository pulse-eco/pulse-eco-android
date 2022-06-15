package com.netcetera.skopjepulse.historyforecast.calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.map.MapFragment
import kotlinx.android.synthetic.main.calendar_dialog.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList


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
  private val currentMonthRequestRes = MapFragment.CALENDAR_ITEM_RESULT

  @SuppressLint("LogNotTimber")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    //Set calendar
    update(null)

    Log.d("RESULT",currentMonthRequestRes.toString())

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
      requireContext().getString(R.string.december).substring(0, 3))

    val arrayOfYear = arrayOf("2017", "2018", "2019", "2020", "2021", "2022")
    calendarCancelButton()

    calendarPreviousArrow.setOnClickListener {  update(CalendarAdapter.DATE_INPUT) }

    val currentYear = CalendarAdapter.DATE_INPUT?.year.toString()
    val month = CalendarAdapter.DATE_INPUT?.month?.name
    val currentMonth = month?.substring(0, 1)?.toUpperCase() + month?.substring(1)?.toLowerCase()
    calendarMonthYearText.setOnClickListener {
      calendarNextArrowUnavailable.visibility = View.GONE
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
      monthYearPickerRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
      monthYearPickerRecyclerView.adapter = monthAdapter
      monthYearPickerRecyclerView.suppressLayout(true)
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
        showCalendarHideRecyclerView(currentMonth,currentYear)
        calendarCancelButton()
      }
      calendarDialogCancelButton.setOnClickListener {
        showCalendarHideRecyclerView(currentMonth,currentYear)
        calendarCancelButton()
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
        monthYearPickerRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        val yearAdapter = CalendarMonthYearPickerAdapter(requireContext(), arrayOfYear)
        monthYearPickerRecyclerView.adapter = yearAdapter
        monthYearPickerRecyclerView.suppressLayout(true)
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
          calendarCancelButtonFromMonthAndYearPicker(currentMonth,currentYear)
        }
      }
    }
  }

  private fun nextMonthClick(dateInput: LocalDate?) {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
    val today = LocalDate.parse("01/06/2022", formatter)

    if (dateInput != null){
      val next = dateInput.plusMonths(1)
      val nextMonth = next.month.name
      val year = next.year.toString()
      val nextDow = next.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
      val lengthOfNextMonth = next.lengthOfMonth()
      val intValueDow = intValueForDayOfWeek(nextDow)
      CalendarAdapter.DATE_INPUT = next


      val monthText = nextMonth.substring(0,1).toUpperCase() + nextMonth.substring(1).toLowerCase()
      val monthYearText = "${newMonth ?: monthText} ${newYear ?: year}"
      calendarMonthYearText.text = monthYearText

      val list = ArrayList<CalendarTry>()
      for (i in 0 until intValueDow) {
        list.add(CalendarTry(0, 3))
      }
      for (j in 1..lengthOfNextMonth) {
        list.add(CalendarTry(j, 3))
      }

      if((today.month.name > next.month.name && today.year > next.year) || (today.month.name == next.month.name && today.year == next.year)){
        calendarNextArrow.visibility = View.GONE
        calendarNextArrowUnavailable.visibility = View.VISIBLE
      }
      setUpCalendarRecyclerView(list,intValueDow,dateInput)
      calendarPreviousArrow.setOnClickListener {
        update(next)
      }

      calendarNextArrow.setOnClickListener {
        nextMonthClick(next)
      }
    }
  }

  private fun setUpCalendarRecyclerView(list:ArrayList<CalendarTry>,intValueDow: Int,dateInput: LocalDate){
    val layoutManager = object : GridLayoutManager(context, 7) {
      override fun supportsPredictiveItemAnimations(): Boolean {
        return false
      }
    }
    val recyclerView = calendarRecyclerView
    recyclerView.layoutManager = layoutManager
    recyclerView.adapter = CalendarAdapter(requireContext(), list, intValueDow, dateInput,currentMonthRequestRes)
    recyclerView.suppressLayout(true)

  }

  private fun calendarCancelButtonFromMonthAndYearPicker(currentMonth: String?,currentYear: String) {
    calendarDialogCancelButton.setOnClickListener {
      showCalendarHideRecyclerView(currentMonth,currentYear)
      calendarCancelButton()
    }
  }

  private fun update(date: LocalDate?)
  {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
    val today = LocalDate.parse("01/06/2022", formatter)

    if (date == null) {
      val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
      val date = LocalDate.parse("01/06/2022", formatter)
      CalendarAdapter.DATE_INPUT = date
      val dow: DayOfWeek = date.dayOfWeek
      val lengthOfMonth = date.lengthOfMonth()
      val output: String = dow.getDisplayName(TextStyle.SHORT, Locale.US)
      val intValueDow = intValueForDayOfWeek(output)
      calendarNextArrow.visibility = View.GONE
      calendarNextArrowUnavailable.visibility = View.VISIBLE

      val monthValue = date.month.name
      val currentMonth = monthValue.substring(0,1).toUpperCase() + monthValue.substring(1).toLowerCase()
      val currentYear = date.year.toString()

      val currentMonthYear = "${newMonth ?: currentMonth} ${newYear ?: currentYear}"
      calendarMonthYearText.text = currentMonthYear

      val list = ArrayList<CalendarTry>()
      for (i in 0 until intValueDow) {
        list.add(CalendarTry(0, 3))
      }
      for (j in 1..lengthOfMonth) {
        list.add(CalendarTry(j, 3))
      }

      val layoutManager = object : GridLayoutManager(context, 7) {
        override fun supportsPredictiveItemAnimations(): Boolean {
          return false
        }
      }
      setUpCalendarRecyclerView(list,intValueDow,date)

    }else {
      calendarNextArrow.visibility = View.VISIBLE
      calendarNextArrowUnavailable.visibility = View.GONE
      calendarRecyclerView.visibility = View.GONE
      calendarMonthYearText.visibility = View.GONE
      calendarNextArrow.alpha = 1F
      val prev = date.minusMonths(1)
      val prevMonth = prev?.month?.name
      val prevYear = prev?.year.toString()
      CalendarAdapter.DATE_INPUT = prev
      val prevDay = prev.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)

      val prevDayOfWeek = prev?.dayOfWeek?.name?.substring(0, 3)
      val prevDayInput = prevDayOfWeek?.substring(0, 1)?.toUpperCase() + prevDayOfWeek?.substring(1)?.toLowerCase()

      val lengthOfMonth = prev?.lengthOfMonth()
      val dayOfWeekIntValue = intValueForDayOfWeek(prevDay)

      val list = ArrayList<CalendarTry>()
      for (i in 0 until dayOfWeekIntValue) {
        list.add(CalendarTry(0, 3))
      }
      for (j in 1..lengthOfMonth!!) {
        list.add(CalendarTry(j, 3))
      }
      setUpCalendarRecyclerView(list,dayOfWeekIntValue,prev)
      calendarMonthYearText.text = "${prevMonth?.substring(0, 1)?.toUpperCase() + prevMonth?.substring(1)?.toLowerCase()} ${prevYear}"
      calendarMonthYearText.visibility = View.VISIBLE
      calendarRecyclerView.visibility = View.VISIBLE

      if((today.month.name > prev.month.name && today.year > prev.year) || (today.month.name == prev.month.name && today.year == prev.year)){
        calendarNextArrow.visibility = View.GONE
        calendarNextArrowUnavailable.visibility = View.VISIBLE
      }

      calendarPreviousArrow.setOnClickListener {
        update(prev)
      }
      calendarNextArrowUnavailable.visibility = View.GONE
      calendarNextArrow.visibility = View.VISIBLE

      calendarNextArrow.setOnClickListener {
        nextMonthClick(prev)
      }
    }


  }


  private fun showCalendarHideRecyclerView(currentMonth: String?,currentYear: String) {
    calendarPreviousArrow.visibility = View.VISIBLE
    calendarNextArrow.visibility = View.VISIBLE
    calendarHeader.visibility = View.VISIBLE
    calendarYearPicker.visibility = View.GONE
    calendarMonthYearText.visibility = View.VISIBLE
    calendarMonthYearText.text = "${newMonth ?: currentMonth} ${newYear ?: currentYear}"
    calendarLine.visibility = View.VISIBLE
    calendarDialogOkButton.visibility = View.VISIBLE
    calendarRecyclerView.visibility = View.VISIBLE
    monthYearPickerRecyclerView.visibility = View.GONE
    calendarYearPicker.visibility = View.GONE
    update(null)

  }

  private fun calendarCancelButton() {
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


