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


  private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
  private val currentMonthRequestRes = MapFragment.calendarValuesResult
//  private val mainViewModel: MainViewModel by sharedViewModel()
//  val mapViewModel: MapViewModel by sharedViewModel { parametersOf(city) }
//  val city: City by lazy { requireArguments().getParcelable("city")!! }

  companion object {
    var newMonth: String? = null
    var newYear: String? = null
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
    var toDate  = LocalDate.parse("17/06/2022", formatter)
    var fromDate = toDate.minusDays(8)
  }
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.calendar_dialog, container)
  }

  @SuppressLint("LogNotTimber")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    //Set calendar
    CalendarAdapter.DATE_INPUT = null
    update(CalendarAdapter.DATE_INPUT)


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
    val monthValue = CalendarAdapter.DATE_INPUT?.month?.value
    val yearValue = CalendarAdapter.DATE_INPUT?.year
    val currentMonth = month?.substring(0, 1)?.toUpperCase() + month?.substring(1)?.toLowerCase()
    calendarMonthYearText.setOnClickListener {
      calendarNextArrowUnavailable.visibility = View.GONE
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
        val newDate = LocalDate.parse("01/${getMonthNumber(newMonth!!)}/${newYear ?: yearValue}",formatter)
        updateFromMonthYearPicker(newDate)

        calendarMonthYearText.visibility = View.VISIBLE
        val month = CalendarAdapter.DATE_INPUT?.month.toString()
        val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
        calendarMonthYearText.text = "$monthFirstUpper ${CalendarAdapter.DATE_INPUT?.year}"
        calendarPreviousArrow.visibility = View.VISIBLE
        calendarNextArrow.visibility = View.VISIBLE
        calendarHeader.visibility = View.VISIBLE
        calendarRecyclerView.visibility = View.VISIBLE
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
          val monthAdapter = CalendarMonthYearPickerAdapter(requireContext(), arrayOfMonths)
          monthYearPickerRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
          monthYearPickerRecyclerView.adapter = monthAdapter
          monthYearPickerRecyclerView.suppressLayout(true)
          monthAdapter.onItemClick = {
            newMonth = getFullMonthName(CalendarMonthYearPickerAdapter.MONTH_YEAR_VALUE)
            calendarLine.visibility = View.VISIBLE
            calendarYearPicker.visibility = View.GONE
            val newInputs = "${newMonth ?: currentMonth} ${newYear ?: currentYear}"

            val newDate = LocalDate.parse("01/${getMonthNumber(newMonth!!) ?: monthValue }/${newYear ?: yearValue}",formatter)
            Log.d("New date",newDate.toString())
            updateFromMonthYearPicker(newDate)

            calendarMonthYearText.visibility = View.VISIBLE
            calendarMonthYearText.text = newInputs
            calendarPreviousArrow.visibility = View.VISIBLE
            calendarNextArrow.visibility = View.VISIBLE
            calendarHeader.visibility = View.VISIBLE
            calendarRecyclerView.visibility = View.VISIBLE
            monthYearPickerRecyclerView.visibility = View.GONE
            showCalendarHideRecyclerView(currentMonth,currentYear)
            calendarCancelButton()
            monthAdapter.onItemClick ={
              calendarLine.visibility = View.VISIBLE
              calendarYearPicker.visibility = View.GONE
              calendarMonthYearText.visibility = View.VISIBLE
              val month = CalendarAdapter.DATE_INPUT?.month.toString()
              val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
              calendarMonthYearText.text = "$monthFirstUpper ${CalendarAdapter.DATE_INPUT?.year}"
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
    }
  }

  private fun nextMonthClick(dateInput: LocalDate?) {
    val today = LocalDate.parse("01/06/2022", formatter)

    if (dateInput != null){
      val next = dateInput.plusMonths(1)
      val nextDow = next.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
      val lengthOfNextMonth = next.lengthOfMonth()
      val intValueDow = intValueForDayOfWeek(nextDow)
      CalendarAdapter.DATE_INPUT = next

      val month = CalendarAdapter.DATE_INPUT?.month.toString()
      val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
      calendarMonthYearText.text = "$monthFirstUpper ${CalendarAdapter.DATE_INPUT?.year}"

      val list = ArrayList<CalendarItemsDataModel>()
      for (i in 0 until intValueDow) {
        list.add(CalendarItemsDataModel(0, intValueDow))
      }
      for (j in 1..lengthOfNextMonth) {
        list.add(CalendarItemsDataModel(j, intValueDow))
      }

      if((today.month.name > next.month.name && today.year > next.year) || (today.month.name == next.month.name && today.year == next.year)){
        calendarNextArrow.visibility = View.GONE
        calendarNextArrowUnavailable.visibility = View.VISIBLE
      }
      setUpCalendarRecyclerView(list,dateInput)
      calendarPreviousArrow.setOnClickListener {
        update(next)
      }

      calendarNextArrow.setOnClickListener {
        nextMonthClick(next)
      }
    }
  }

  private fun setUpCalendarRecyclerView(list:ArrayList<CalendarItemsDataModel>, dateInput: LocalDate){
    val layoutManager = object : GridLayoutManager(context, 7) {
      override fun supportsPredictiveItemAnimations(): Boolean {
        return false
      }
    }
    val recyclerView = calendarRecyclerView
    recyclerView.layoutManager = layoutManager
    val adapter =  CalendarAdapter(requireContext(), list, dateInput,currentMonthRequestRes,MapFragment.bandValueOverallData)
    recyclerView.adapter = adapter
    adapter.onItemClick = {
      val clickedDate  = LocalDate.parse(CalendarAdapter.DATE_CLICKED, Companion.formatter)
      val fromClickedDate = clickedDate.plusDays(4)
      toDate = fromClickedDate
      fromDate = toDate.minusDays(8)
      dismiss()
    }
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
    val today = LocalDate.parse("01/06/2022", formatter)

    if (date == null) {
      val date = LocalDate.parse("01/06/2022", formatter)
      CalendarAdapter.DATE_INPUT = date
      val dow: DayOfWeek = date.dayOfWeek
      val lengthOfMonth = date.lengthOfMonth()
      val output: String = dow.getDisplayName(TextStyle.SHORT, Locale.US)
      val intValueDow = intValueForDayOfWeek(output)
      calendarNextArrow.visibility = View.GONE
      calendarNextArrowUnavailable.visibility = View.VISIBLE

      val month = CalendarAdapter.DATE_INPUT?.month.toString()
      val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
      calendarMonthYearText.text = "$monthFirstUpper ${CalendarAdapter.DATE_INPUT?.year}"


      val list = ArrayList<CalendarItemsDataModel>()
      for (i in 0 until intValueDow) {
        list.add(CalendarItemsDataModel(0, intValueDow))
      }
      for (j in 1..lengthOfMonth) {
        list.add(CalendarItemsDataModel(j, intValueDow))
      }
      setUpCalendarRecyclerView(list,date)

    }else {
      calendarNextArrow.visibility = View.VISIBLE
      calendarNextArrowUnavailable.visibility = View.GONE
      calendarRecyclerView.visibility = View.GONE
      calendarMonthYearText.visibility = View.GONE
      val prev = date.minusMonths(1)
      CalendarAdapter.DATE_INPUT = prev
      val prevDay = prev.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)


      val lengthOfMonth = prev?.lengthOfMonth()
      val dayOfWeekIntValue = intValueForDayOfWeek(prevDay)

      val list = ArrayList<CalendarItemsDataModel>()
      for (i in 0 until dayOfWeekIntValue) {
        list.add(CalendarItemsDataModel(0, dayOfWeekIntValue))
      }
      for (j in 1..lengthOfMonth!!) {
        list.add(CalendarItemsDataModel(j, dayOfWeekIntValue))
      }

      setUpCalendarRecyclerView(list,prev)
      val month = CalendarAdapter.DATE_INPUT?.month.toString()
      val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
      calendarMonthYearText.text = "$monthFirstUpper ${CalendarAdapter.DATE_INPUT?.year}"

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
    val month = CalendarAdapter.DATE_INPUT?.month.toString()
    val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
    calendarMonthYearText.text = "$monthFirstUpper ${CalendarAdapter.DATE_INPUT?.year}"
    calendarLine.visibility = View.VISIBLE
    calendarRecyclerView.visibility = View.VISIBLE
    monthYearPickerRecyclerView.visibility = View.GONE
    calendarYearPicker.visibility = View.GONE
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
        return 1
      }
      getString(R.string.february) -> {
        return 2
      }
      getString(R.string.march) -> {
        return 3
      }
      getString(R.string.april) -> {
        return 4
      }
      getString(R.string.may) -> {
        return 5
      }
      getString(R.string.june) -> {
        return 6
      }
      getString(R.string.july) -> {
        return 7
      }
      getString(R.string.august) -> {
        return 8
      }
      getString(R.string.september) -> {
        return 9
      }
      getString(R.string.october) -> {
        return 10
      }
      getString(R.string.november) -> {
        return 11
      }
      getString(R.string.december) -> {
        return 12
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

  private fun updateFromMonthYearPicker(date: LocalDate){
    CalendarAdapter.DATE_INPUT = date
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
    val intValueDayOfWeek = intValueForDayOfWeek(dayOfWeek)
    val lengthOfMonth = date.lengthOfMonth()

    if((CalendarAdapter.DATE_INPUT_TODAY.month.name > date.month.name && CalendarAdapter.DATE_INPUT_TODAY.year > date.year)
      || (CalendarAdapter.DATE_INPUT_TODAY.month.name == date.month.name && CalendarAdapter.DATE_INPUT_TODAY.year == date.year)){
      calendarNextArrow.visibility = View.GONE
      calendarNextArrowUnavailable.visibility = View.VISIBLE
    }

    calendarRecyclerView.visibility = View.GONE
    calendarMonthYearText.visibility = View.GONE
    CalendarAdapter.DATE_INPUT = date

    val listOfDaysMonth = ArrayList<CalendarItemsDataModel>()
    for (i in 0 until intValueDayOfWeek){
      listOfDaysMonth.add(CalendarItemsDataModel(0,intValueDayOfWeek))
    }
    for (j in 1..lengthOfMonth){
      listOfDaysMonth.add(CalendarItemsDataModel(j,intValueDayOfWeek))
    }
    setUpCalendarRecyclerView(listOfDaysMonth,date)
    val month = CalendarAdapter.DATE_INPUT?.month.toString()
    val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
    calendarMonthYearText.text = "$monthFirstUpper ${CalendarAdapter.DATE_INPUT?.year}"
    calendarMonthYearText.visibility = View.VISIBLE
    calendarRecyclerView.visibility = View.VISIBLE

  }
}


